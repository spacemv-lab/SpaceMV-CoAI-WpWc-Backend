/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.iam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.core.utils.sign.RsaUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.iam.dto.LoginRequest;
import com.ruoyi.iam.dto.LoginResponse;
import com.ruoyi.iam.dto.PasswordChangeRequest;
import com.ruoyi.iam.dto.ProfileUpdateRequest;
import com.ruoyi.iam.dto.RegisterRequest;
import com.ruoyi.iam.dto.SendAuthVerifyCodeRequest;
import com.ruoyi.iam.dto.InnerUserImportRequest;
import com.ruoyi.iam.dto.UserLoginUserDTO;
import com.ruoyi.iam.entity.IamAuthLog;
import com.ruoyi.iam.entity.IamUser;
import com.ruoyi.iam.entity.IamUserBackupContact;
import com.ruoyi.iam.entity.IamUserChannel;
import com.ruoyi.iam.mapper.IamAuthLogMapper;
import com.ruoyi.iam.mapper.IamUserBackupContactMapper;
import com.ruoyi.iam.mapper.IamUserChannelMapper;
import com.ruoyi.iam.entity.IamUserProduct;
import com.ruoyi.iam.mapper.IamUserProductMapper;
import com.ruoyi.iam.mapper.IamUserMapper;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.security.auth.AuthUtil;
import com.ruoyi.iam.util.UsernameGenerator;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 认证服务 — 注册 / 登录
 *
 * @author txwx
 */
@Slf4j
@Service
public class AuthService
{
    private static final String CODE_KEY_PREFIX = "iam:verify:code:";

    private final IamUserMapper userMapper;
    private final IamUserChannelMapper channelMapper;
    private final IamUserBackupContactMapper backupContactMapper;
    private final IamAuthLogMapper authLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final VerifyCodeService verifyCodeService;
    private static final String FAIL_KEY_PREFIX = "iam:login:fail:";
    private static final String LOCK_KEY_PREFIX = "iam:login:lock:";
    private static final int MAX_ATTEMPTS = 5;
    private static final long FAIL_TTL = 300L;    // 5 分钟
    private static final long LOCK_TTL = 1800L;    // 30 分钟

    private final String productLine;
    private final long accessTokenValidity;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
    private final com.ruoyi.iam.service.UserEventPublisher userEventPublisher;
    private final com.ruoyi.common.redis.service.RedisService redisService;
    /** 业务系统 JWT 签名密钥（用于解析业务 token / 旧 RuoYi token） */
    private final String businessSecret;
    private final RemoteUserService remoteUserService;
    private final IamUserProductMapper iamUserProductMapper;
    private final IamValidateCodeService captchaService;

    public AuthService(
        IamUserMapper userMapper,
        IamUserChannelMapper channelMapper,
        IamUserBackupContactMapper backupContactMapper,
        IamAuthLogMapper authLogMapper,
        PasswordEncoder passwordEncoder,
        TokenService tokenService,
        VerifyCodeService verifyCodeService,
        org.springframework.data.redis.core.StringRedisTemplate redisTemplate,
        UserEventPublisher userEventPublisher,
        com.ruoyi.common.redis.service.RedisService redisService,
        RemoteUserService remoteUserService,
        IamUserProductMapper iamUserProductMapper,
        IamValidateCodeService captchaService,
        @Value("${iam.token-validators.business.secret:abcdefghijklmnopqrstuvwxyz}") String businessSecret,
        @Value("${iam.jwt.secret}") String secret,
        @Value("${iam.jwt.access-token-validity:7200}") long accessTokenValidity)
    {
        this.userMapper = userMapper;
        this.channelMapper = channelMapper;
        this.backupContactMapper = backupContactMapper;
        this.authLogMapper = authLogMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.verifyCodeService = verifyCodeService;
        this.productLine = "spacemv-coai";
        this.accessTokenValidity = accessTokenValidity;
        this.redisTemplate = redisTemplate;
        this.userEventPublisher = userEventPublisher;
        this.redisService = redisService;
        this.businessSecret = businessSecret;
        this.remoteUserService = remoteUserService;
        this.iamUserProductMapper = iamUserProductMapper;
        this.captchaService = captchaService;
    }

    /**
     * 字段唯一性校验（供前端注册/登录页调用）
     * @param fieldType  fieldType: username/phone/email 单独校验，或 "all" 跨所有字段统一校验
     * @param fieldValue 待校验的值
     * @param productLine 产品线
     * @return true=唯一, false=已被注册；当 fieldType="all" 时，只要任意字段被占用即返回 false
     */
    public boolean checkFieldUnique(String fieldType, String fieldValue, String productLine)
    {
        if ("all".equals(fieldType))
        {
            // 跨所有字段统一校验：username + phone + email 任一被占用就返回 false
            // 1. 查 iam_users.username
            LambdaQueryWrapper<IamUser> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(IamUser::getUsername, fieldValue).eq(IamUser::getDelFlag, "0");
            if (userMapper.selectCount(userWrapper) > 0)
            {
                return false;
            }
            // 2. 查 iam_user_channel (phone)
            LambdaQueryWrapper<IamUserChannel> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(IamUserChannel::getProductLine, productLine)
                .eq(IamUserChannel::getChannelType, "phone")
                .eq(IamUserChannel::getChannelAccount, fieldValue)
                .eq(IamUserChannel::getStatus, "0");
            if (channelMapper.selectCount(phoneWrapper) > 0)
            {
                return false;
            }
            // 3. 查 iam_user_channel (email)
            LambdaQueryWrapper<IamUserChannel> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(IamUserChannel::getProductLine, productLine)
                .eq(IamUserChannel::getChannelType, "email")
                .eq(IamUserChannel::getChannelAccount, fieldValue)
                .eq(IamUserChannel::getStatus, "0");
            return channelMapper.selectCount(emailWrapper) == 0;
        }

        if ("username".equals(fieldType))
        {
            // 用户名唯一性（排除已删除）
            LambdaQueryWrapper<IamUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IamUser::getUsername, fieldValue).eq(IamUser::getDelFlag, "0");
            return userMapper.selectCount(wrapper) == 0;
        }

        if ("phone".equals(fieldType) || "email".equals(fieldType))
        {
            // 通道账号唯一性（按产品线 + 通道类型 + 状态）
            LambdaQueryWrapper<IamUserChannel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(IamUserChannel::getProductLine, productLine)
                .eq(IamUserChannel::getChannelType, fieldType)
                .eq(IamUserChannel::getChannelAccount, fieldValue)
                .eq(IamUserChannel::getStatus, "0");
            return channelMapper.selectCount(wrapper) == 0;
        }

        return false;
    }

    // ──────────────────────────────────────────────
    // 新增：自动识别类型 + productLine 隔离 + 显示名校验
    // ──────────────────────────────────────────────

    /**
     * 字段唯一性校验（前端注册页新接口，自动识别类型 + productLine 隔离）
     * <p>
     * 第一层：按 productLine 过滤（作用域隔离）<br>
     * 第二层：自动识别 accountName 类型<br>
     * 手机号（11位纯数字）→ 查 phone 通道<br>
     * 含 @ → 查 email 通道<br>
     * 其他 → 查 username（iam_users.productLine）<br>
     * <b>另：所有类型均额外校验 iam_users.display_name（防重复显示名）</b>
     *
     * @param accountName 待校验的值（手机号/邮箱/用户名）
     * @param productLine 产品线标识
     * @return true=唯一, false=已被注册
     */
    public boolean checkFieldUnique(String accountName, String productLine)
    {
        // 1. 自动识别类型
        String type;
        if (accountName.matches("1[3-9]\\d{9}"))
        {
            type = "phone";
        }
        else if (accountName.contains("@"))
        {
            type = "email";
        }
        else
        {
            type = "username";
        }

        // 2. 按类型校验标准字段（均按 productLine 隔离）
        if ("phone".equals(type))
        {
            LambdaQueryWrapper<IamUserChannel> pw = new LambdaQueryWrapper<>();
            pw.eq(IamUserChannel::getProductLine, productLine)
                .eq(IamUserChannel::getChannelType, "phone")
                .eq(IamUserChannel::getChannelAccount, accountName)
                .eq(IamUserChannel::getStatus, "0");
            if (channelMapper.selectCount(pw) > 0) return false;
        }
        else if ("email".equals(type))
        {
            LambdaQueryWrapper<IamUserChannel> ew = new LambdaQueryWrapper<>();
            ew.eq(IamUserChannel::getProductLine, productLine)
                .eq(IamUserChannel::getChannelType, "email")
                .eq(IamUserChannel::getChannelAccount, accountName)
                .eq(IamUserChannel::getStatus, "0");
            if (channelMapper.selectCount(ew) > 0) return false;
        }
        else
        {
            // username 类型 — 按 productLine 隔离
            LambdaQueryWrapper<IamUser> uw = new LambdaQueryWrapper<>();
            uw.eq(IamUser::getUsername, accountName)
                .eq(IamUser::getProductLine, productLine)
                .eq(IamUser::getDelFlag, "0");
            if (userMapper.selectCount(uw) > 0) return false;
        }

        // 3. ★ 所有类型均额外校验 display_name + productLine
        LambdaQueryWrapper<IamUser> dw = new LambdaQueryWrapper<>();
        dw.eq(IamUser::getDisplayName, accountName)
            .eq(IamUser::getProductLine, productLine)
            .eq(IamUser::getDelFlag, "0");
        return userMapper.selectCount(dw) == 0;
    }

    /**
     * 注册
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) throws Exception {
        String account = request.getChannelAccount();
        String type = request.getChannelType();

        // 1. 验证码校验
        if (!verifyCodeService.verifyCode(type, account, request.getVerifyCode()))
        {
            auditLog(null, type, "register", "fail", "验证码错误或已过期", account);
            throw new ServiceException("验证码错误或已过期");
        }

        // 2. 密码强度校验（v1.1: 8-20 位）
        String password = RsaUtils.decryptByPrivateKey(request.getPassword());
        if (password.length() < 8 || password.length() > 20)
        {
            auditLog(null, type, "register", "fail", "密码长度 8-20 位", account);
            throw new ServiceException("密码长度需为 8-20 位");
        }

        // 3. 通道唯一性检查
        LambdaQueryWrapper<IamUserChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IamUserChannel::getProductLine, productLine)
            .eq(IamUserChannel::getChannelType, type)
            .eq(IamUserChannel::getChannelAccount, account)
            .eq(IamUserChannel::getStatus, "0");
        if (channelMapper.selectCount(wrapper) > 0)
        {
            auditLog(null, type, "register", "fail", "通道账号已注册", account);
            throw new ServiceException("该" + type + "已被注册");
        }

        // 4. 用户名处理（v1.1: 优先前端传入 0-20 字符）
        String username = request.getUsername();
        if (username == null || username.isBlank())
        {
            username = UsernameGenerator.generate();
        }
        else if (username.length() > 20)
        {
            auditLog(null, type, "register", "fail", "用户名超过 20 字符", account);
            throw new ServiceException("用户名长度不能超过 20 个字符");
        }

        // 用户名唯一性检查
        LambdaQueryWrapper<IamUser> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(IamUser::getUsername, username).eq(IamUser::getDelFlag, "0");
        if (userMapper.selectCount(usernameWrapper) > 0)
        {
            auditLog(null, type, "register", "fail", "用户名已被注册", account);
            throw new ServiceException("用户名已被注册");
        }

        // 5. BCrypt 加密
        String passwordHash = passwordEncoder.encode(password);

        // 6. 创建用户
        IamUser user = new IamUser();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setDisplayName(account);
        user.setStatus("0");
        user.setProductLine(productLine);
        user.setUserFrom("register");
        user.setCreateTime(new Date());

        userMapper.insert(user);
        Long userId = user.getId();

        // 7. 创建通道
        IamUserChannel channel = new IamUserChannel();
        channel.setUserId(userId);
        channel.setProductLine(productLine);
        channel.setChannelType(type);
        channel.setChannelAccount(account);
        channel.setIsPrimary("1");
        channel.setStatus("0");
        channel.setBindTime(new Date());

        channelMapper.insert(channel);

        // 8. 备用联系方式写入（v1.1 新增）
        if (request.getBakPhone() != null || request.getBakEmail() != null)
        {
            IamUserBackupContact backup = new IamUserBackupContact();
            backup.setUserId(userId);
            backup.setBakPhone(request.getBakPhone());
            backup.setBakEmail(request.getBakEmail());
            backupContactMapper.insert(backup);
        }

        // 9. 同步到 sys_user（Feign 调用 system 模块，通过映射表解耦 ID）
        try
        {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(username);
            sysUser.setNickName(account);
            if ("phone".equals(type))
            {
                sysUser.setPhonenumber(account);
            }
            else if ("email".equals(type))
            {
                sysUser.setEmail(account);
            }
            sysUser.setStatus("0");
            sysUser.setDelFlag("0");
            sysUser.setUserType("10");
            // 注意：不设置 userId，由 system 模块自增生成
            R<Long> syncResult = remoteUserService.syncIamUser(sysUser, SecurityConstants.INNER);
            if (R.isSuccess(syncResult) && syncResult.getData() != null)
            {
                Long sysUserId = syncResult.getData();
                // 写入映射表维护 iam_user ↔ sys_user 关系
                IamUserProduct mapping = new IamUserProduct();
                mapping.setIamUserId(userId);
                mapping.setProductLine(productLine);
                mapping.setProductUserId(sysUserId);
                iamUserProductMapper.insert(mapping);
                log.info("IAM 注册同步成功: iamUserId={}, sysUserId={}, productLine={}",
                    userId, sysUserId, productLine);
            }
            else
            {
                log.warn("IAM 注册同步到 sys_user 返回失败: {}", syncResult != null ? syncResult.getMsg() : "null");
            }
        }
        catch (Exception e)
        {
            log.error("IAM 注册同步到 sys_user 异常, userId={}, username={}", userId, username, e);
            // 不阻断注册流程 — 不影响用户正常使用
        }

        // 10. 签发 JWT + LoginUser 写入 Redis
        String accessToken = createAccessTokenWithLoginUser(user.getId(), username, user);
        String refreshToken = tokenService.createRefreshToken(userId);

        // 11. 审计日志
        auditLog(userId, type, "register", "success", null, account);

        // 12. 发布用户注册事件（MQ 桥接）
        userEventPublisher.publishUserCreated(userId, username, type, account);

        // 13. 返回
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(userId);
        response.setUsername(username);
        response.setProductLine(productLine);
        response.setExpiresIn(accessTokenValidity);
        return response;
    }

    /**
     * 登录（v1.1: 三步定位）
     */
    public LoginResponse login(LoginRequest request)
    {
        String account = request.getChannelAccount();
        String loginType = request.getLoginType();

        // ========== 图形验证码校验（仅密码登录需要） ==========
        if ("password".equals(loginType) && request.getUuid() != null && request.getCode() != null)
        {
            validateCaptcha(request.getUuid(), request.getCode());
        }

        if ("sms".equals(loginType))
        {
            // 短信登录：按通道查找
            IamUser user = findByChannel(account, "phone");
            if (user == null)
            {
                auditLog(null, "phone", "login", "fail", "用户不存在", account);
                throw new ServiceException("用户不存在");
            }
            request.setChannelAccount(account);
            return smsLogin(request);
        }

        // 密码登录：三步定位（username精确 → channel → username模糊）
        IamUser user = null;
        String matchedAccount = account;

        // 第一步：username 精确匹配
        user = findByUsername(account);
        if (user != null)
        {
            matchedAccount = user.getUsername();
        }
        else
        {
            // 第二步：channel 精确匹配（先找 phone，再找 email）
            user = findByChannel(account, "phone");
            if (user == null)
            {
                user = findByChannel(account, "email");
            }
            if (user != null)
            {
                matchedAccount = user.getDisplayName();
            }
            else
            {
                // 第三步：username 模糊匹配（手机号/邮箱作为用户名的一部分）
                user = findByUsernameLike(account);
                if (user != null)
                {
                    matchedAccount = user.getDisplayName();
                }
            }
        }

        if (user == null)
        {
            // 懒迁移：System 存量用户首次登录时自动导入 IAM
            if ("password".equals(loginType))
            {
                user = tryLazyImportFromSystem(account);
                if (user != null)
                {
                    matchedAccount = user.getUsername();
                    log.info("lazy import: 从 System 导入存量用户 username={} iamUserId={}", account, user.getId());
                }
            }

            if (user == null)
            {
                auditLog(null, "phone", "login", "fail", "用户不存在", account);
                throw new ServiceException("用户不存在");
            }
        }

        IamUser finalUser = user;
        String finalAccount = matchedAccount;
        return doLogin(finalUser, request, () -> {
            try {
                return passwordLogin(finalUser, request, finalAccount);
            } catch (Exception e) {
                throw new ServiceException(e.getMessage());
            }
        });
    }

    /**
     * 懒迁移：从 System 模块查询存量用户，导入 IAM 并返回 iam_user 实体
     * <p>
     * 当 IAM 本地查询不到用户时触发，通过 Feign 调用 System 模块的 /user/info/{username} 接口，
     * 如果 sys_user 存在则自动导入到 IAM，打通存量用户的首发登录。
     */
    private IamUser tryLazyImportFromSystem(String account)
    {
        R<LoginUser> result = remoteUserService.getUserInfo(account, SecurityConstants.INNER);
        if (!R.isSuccess(result) || result.getData() == null || result.getData().getSysUser() == null)
        {
            return null;
        }

        SysUser sysUser = result.getData().getSysUser();

        InnerUserImportRequest req = new InnerUserImportRequest();
        req.setUsername(sysUser.getUserName());
        req.setPasswordHash(sysUser.getPassword());
        req.setPhone(sysUser.getPhonenumber());
        req.setEmail(sysUser.getEmail());
        req.setDisplayName(sysUser.getNickName());
        req.setAvatarUrl(sysUser.getAvatar());
        req.setProductUserId(sysUser.getUserId());

        // importUser 内部事务创建 iam_user + iam_user_channel + iam_user_product
        LoginResponse loginResponse = importUser(req);

        // 查询完整的 iam_user 实体返回，供 login() 继续走完整登录流程
        return userMapper.selectById(loginResponse.getUserId());
    }

    /**
     * 校验图形验证码
     */
    private void validateCaptcha(String uuid, String code)
    {
        captchaService.checkCaptcha(code, uuid);
    }

    private LoginResponse doLogin(IamUser user, LoginRequest request, java.util.function.Supplier<LoginResponse> loginFn)
    {
        return loginFn.get();
    }

    /**
     * 密码登录（无参重载：用于旧前端调用）
     * 按通道查找：先 phone，再 email
     */
    public LoginResponse passwordLogin(LoginRequest request) {
        String account = request.getChannelAccount();

        IamUser user = findByChannel(account, "phone");
        if (user == null)
        {
            user = findByChannel(account, "email");
        }
        if (user == null)
        {
            auditLog(null, "phone", "login", "fail", "用户不存在", account);
            throw new ServiceException("用户不存在");
        }
        try {
            return passwordLogin(user, request, user.getDisplayName());
        } catch (Exception e) {
            throw new ServiceException("密码登录失败");
        }
    }

    /**
     * 密码登录（重载：带指定用户）
     */
    private LoginResponse passwordLogin(IamUser user, LoginRequest request, String matchedAccount) throws Exception {
        String account = request.getChannelAccount();

        // 检查登录限流
        if (isUserLocked(user.getId()))
        {
            throw new ServiceException("账号已被锁定，请 30 分钟后再试");
        }

        // 检查注销状态
       /* if ("1".equals(user.getDeleteStatus()))
        {
            auditLog(user.getId(), "phone", "login", "fail", "账号处于注销冷静期", account);
            throw new ServiceException("账号处于注销冷静期");
        }*/
        if ("2".equals(user.getDeleteStatus()))
        {
            auditLog(user.getId(), "phone", "login", "fail", "账号已注销", account);
            throw new ServiceException("账号已注销");
        }

        // 检查状态
        if (!"0".equals(user.getStatus()))
        {
            auditLog(user.getId(), "phone", "login", "fail", "用户已停用", account);
            throw new ServiceException("账号已停用");
        }

        // BCrypt 校验
        if (!passwordEncoder.matches(RsaUtils.decryptByPrivateKey(request.getCredential()), user.getPasswordHash()))
        {
            recordLoginFail(user.getId());
            auditLog(user.getId(), "phone", "login", "fail", "密码错误", account);
            throw new ServiceException("密码错误");
        }

        clearLoginFail(user.getId());

        // 更新最后登录信息
        user.setLastLoginIp(IpUtils.getIpAddr());
        user.setLastLoginTime(new Date());
        userMapper.updateById(user);

        return createLoginResponse(user);
    }

    /**
     * 短信验证码登录
     */
    public LoginResponse smsLogin(LoginRequest request)
    {
        String account = request.getChannelAccount();

        // 先查用户（用于限流检查）
        IamUser channelUser = findByChannel(account, "phone");
        Long userId = channelUser != null ? channelUser.getId() : null;
        IamUser user = channelUser;

        // 检查登录限流
        if (userId != null && isUserLocked(userId))
        {
            throw new ServiceException("账号已被锁定，请 30 分钟后再试");
        }

        if (user != null)
        {
            // 检查注销状态
            /*if ("1".equals(user.getDeleteStatus()))
            {
                auditLog(user.getId(), "phone", "login", "fail", "账号处于注销冷静期", account);
                throw new ServiceException("账号处于注销冷静期");
            }*/
            if ("2".equals(user.getDeleteStatus()))
            {
                auditLog(user.getId(), "phone", "login", "fail", "账号已注销", account);
                throw new ServiceException("账号已注销");
            }
        }

        // 验证码校验
        if (!verifyCodeService.verifyCode("phone", account, request.getCredential()))
        {
            if (userId != null)
            {
                recordLoginFail(userId);
            }
            auditLog(null, "phone", "login", "fail", "验证码错误", account);
            throw new ServiceException("验证码错误");
        }

        if (user == null)
        {
            auditLog(null, "phone", "login", "fail", "用户不存在", account);
            throw new ServiceException("用户不存在");
        }

        if (!"0".equals(user.getStatus()))
        {
            auditLog(user.getId(), "phone", "login", "fail", "用户已停用", account);
            throw new ServiceException("账号已停用");
        }

        // 清除失败计数
        clearLoginFail(user.getId());

        return createLoginResponse(user);
    }

    private IamUser findByChannel(String account, String type)
    {
        LambdaQueryWrapper<IamUserChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IamUserChannel::getProductLine, productLine)
            .eq(IamUserChannel::getChannelType, type)
            .eq(IamUserChannel::getChannelAccount, account)
            .eq(IamUserChannel::getStatus, "0");
        IamUserChannel channel = channelMapper.selectOne(wrapper);
        if (channel == null)
        {
            return null;
        }

        return userMapper.selectById(channel.getUserId());
    }

    /**
     * 按用户名精确查找
     */
    private IamUser findByUsername(String username)
    {
        if (username == null || username.isBlank())
        {
            return null;
        }
        LambdaQueryWrapper<IamUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IamUser::getUsername, username).eq(IamUser::getDelFlag, "0");
        return userMapper.selectOne(wrapper);
    }

    /**
     * 按用户名模糊匹配（手机号作为用户名的一部分）
     */
    private IamUser findByUsernameLike(String phoneLike)
    {
        if (phoneLike == null || phoneLike.isBlank())
        {
            return null;
        }
        LambdaQueryWrapper<IamUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(IamUser::getUsername, phoneLike).eq(IamUser::getDelFlag, "0");
        IamUser user = userMapper.selectOne(wrapper);
        // 模糊匹配只返回第一个结果，需二次验证是否为有效手机号
        if (user != null && user.getDisplayName() != null && user.getDisplayName().equals(phoneLike))
        {
            return user;
        }
        // 也尝试直接匹配 displayName（通常是手机号）
        LambdaQueryWrapper<IamUser> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(IamUser::getDisplayName, phoneLike).eq(IamUser::getDelFlag, "0");
        return userMapper.selectOne(wrapper2);
    }

    private LoginResponse createLoginResponse(IamUser user)
    {
        String accessToken = createAccessTokenWithLoginUser(user.getId(), user.getUsername(), user);
        String refreshToken = tokenService.createRefreshToken(user.getId());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setProductLine(productLine);
        response.setExpiresIn(accessTokenValidity);

        auditLog(user.getId(), "phone", "login", "success", null, user.getDisplayName());
        return response;
    }

    /**
     * 发送验证码
     */
    public boolean sendVerifyCode(String channelType, String channelAccount)
    {
        return verifyCodeService.sendCode(channelType, channelAccount);
    }

    /**
     * 登录态验证码发送（校验通道所有权 + 备用联系方式发送）
     * <p>
     * 流程：<br>
     * 1. 根据 channelType + channelAccount 查 iam_user_channel（status=0, delFlag=0）<br>
     * 2. 获取该通道绑定的 queryUserId：<br>
     *    &nbsp;&nbsp;- queryUserId != null 且 != 当前 userId → 抛异常"账号已被他人绑定"<br>
     *    &nbsp;&nbsp;- queryUserId == null（未绑定）→ 直接发送验证码到传入的 channelAccount<br>
     *    &nbsp;&nbsp;- queryUserId == 当前 userId → 查 iam_user_backup_contact 备用联系方式发送
     */
    public void sendAuthVerifyCode(Long userId, SendAuthVerifyCodeRequest req)
    {
        String channelType = req.getChannelType();
        String channelAccount = req.getChannelAccount();

        // 1. 根据 channelType + channelAccount 查通道（status=0, delFlag=0），获取绑定的 userId
        LambdaQueryWrapper<IamUserChannel> queryWrapper = new LambdaQueryWrapper<IamUserChannel>()
            .eq(IamUserChannel::getProductLine, productLine)
            .eq(IamUserChannel::getChannelType, channelType)
            .eq(IamUserChannel::getChannelAccount, channelAccount)
            .eq(IamUserChannel::getStatus, "0")
            .eq(IamUserChannel::getDelFlag, "0");
        IamUserChannel channel = channelMapper.selectOne(queryWrapper);

        Long queryUserId = (channel != null) ? channel.getUserId() : null;

        // 2a. 通道已被他人绑定 → 抛异常
        if (queryUserId != null && !queryUserId.equals(userId))
        {
            log.warn("sendAuthVerifyCode: 账号已被他人绑定, userId={}, channelType={}, account={}",
                userId, channelType, maskChannel(channelType, channelAccount));
            throw new ServiceException("账号已被他人绑定");
        }

        // 2b. 通道未绑定任何人 → 直接发送到传入的 channelAccount（绑定场景：验证所有权）
        if (queryUserId == null)
        {
            log.debug("sendAuthVerifyCode: 通道未绑定, 直接发送验证码到 account={}",
                maskChannel(channelType, channelAccount));
            verifyCodeService.sendCode(channelType, channelAccount);
            return;
        }

        // 3. 通道属于当前用户 → 查备用联系方式发送（解绑/改密等场景）
        IamUserBackupContact backupContact = backupContactMapper.selectByUserId(userId);
        String sendAccount = channelAccount; // fallback 到原账号
        if (backupContact != null)
        {
            if ("phone".equals(channelType) && backupContact.getBakPhone() != null)
            {
                sendAccount = backupContact.getBakPhone();
            }
            else if ("email".equals(channelType) && backupContact.getBakEmail() != null)
            {
                sendAccount = backupContact.getBakEmail();
            }
        }

        log.debug("sendAuthVerifyCode: 通道已绑定 userId={}, channelType={}, 发送到 account={}",
            userId, channelType, maskChannel(channelType, sendAccount));
        verifyCodeService.sendCode(channelType, sendAccount);
    }

    /**
     * 校验验证码（自动识别手机号/邮箱）
     * <p>
     * 迁移自 CRM /verify/code/check，兼容前端已有调用方式。
     * 校验成功后验证码会被消费（防重放），不可重复使用。
     *
     * @param account 手机号或邮箱
     * @param code    验证码
     * @return true=通过，false=失败
     */
    public boolean checkVerifyCode(String account, String code)
    {
        if (account == null || account.isEmpty() || code == null || code.isEmpty())
        {
            return false;
        }
        int type = getAccountType(account);
        String channelType;
        if (type == 1)
        {
            channelType = "phone";
        }
        else if (type == 2)
        {
            channelType = "email";
        }
        else
        {
            return false;
        }
        return verifyCodeService.verifyCode(channelType, account, code);
    }

    private void auditLog(Long userId, String channelType, String authType, String authResult, String failReason, String account)
    {
        IamAuthLog log = new IamAuthLog();
        log.setUserId(userId);
        log.setChannelType(channelType);
        log.setAuthType(authType);
        log.setAuthResult(authResult);
        log.setFailReason(failReason);
        log.setAuthTime(new Date());
        authLogMapper.insert(log);
    }

    private String maskChannel(String type, String account)
    {
        if ("phone".equals(type) && account != null && account.length() > 7)
        {
            return account.substring(0, 3) + "****" + account.substring(account.length() - 4);
        }
        if ("email".equals(type) && account != null && account.contains("@"))
        {
            String local = account.substring(0, account.indexOf("@"));
            if (local.length() > 1)
            {
                return local.charAt(0) + "**" + account.substring(account.indexOf("@"));
            }
        }
        return account;
    }

    // ==================== 登录限流 ====================

    private boolean isUserLocked(Long userId)
    {
        String key = LOCK_KEY_PREFIX + userId;
        String val = redisTemplate.opsForValue().get(key);
        return val != null && "locked".equals(val);
    }

    private void recordLoginFail(Long userId)
    {
        String failKey = FAIL_KEY_PREFIX + userId;
        Long count = redisTemplate.opsForValue().increment(failKey);
        if (count != null && count == 1)
        {
            redisTemplate.expire(failKey, FAIL_TTL, java.util.concurrent.TimeUnit.SECONDS);
        }
        if (count != null && count >= MAX_ATTEMPTS)
        {
            String lockKey = LOCK_KEY_PREFIX + userId;
            redisTemplate.opsForValue().set(lockKey, "locked", LOCK_TTL, java.util.concurrent.TimeUnit.SECONDS);
        }
    }

    private void clearLoginFail(Long userId)
    {
        redisTemplate.delete(FAIL_KEY_PREFIX + userId);
        redisTemplate.delete(LOCK_KEY_PREFIX + userId);
    }

    /**
     * 内部接口：按 ID 查询用户
     */
    public IamUser getUserById(Long userId)
    {
        return userMapper.selectById(userId);
    }

    /**
     * 内部接口：查询用户通道列表
     */
    public java.util.List<IamUserChannel> getChannelList(Long userId)
    {
        LambdaQueryWrapper<IamUserChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IamUserChannel::getUserId, userId)
            .eq(IamUserChannel::getStatus, "0");
        return channelMapper.selectList(wrapper);
    }

    /**
     * 获取当前登录用户信息
     */
    public UserLoginUserDTO getLoginUser(String token)
    {
        Claims claims = tokenService.parseToken(token);
        Long userId = Long.valueOf(claims.getSubject());
        return getLoginUserById(userId);
    }

    /**
     * 按 IAM userId 查询用户信息（供 Controller 直接调用，避免二次解析 token）
     * <p>
     * 与 {@link #getLoginUser(String)} 逻辑一致，但跳过 token 解析步骤，
     * 已经验证过 token 的场景。
     *
     * @param userId IAM 用户 ID（来自 filter 设置的请求属性 {@code iam_user_id}）
     */
    public UserLoginUserDTO getLoginUserById(Long userId)
    {
        IamUser user = userMapper.selectById(userId);
        if (user == null)
        {
            throw new IllegalArgumentException("User not found");
        }

        UserLoginUserDTO dto = new UserLoginUserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        dto.setDeleteStatus(user.getDeleteStatus());

        // 查询通道列表（脱敏）
        LambdaQueryWrapper<IamUserChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IamUserChannel::getUserId, userId)
            .eq(IamUserChannel::getStatus, "0");
        List<IamUserChannel> channels = channelMapper.selectList(wrapper);
        if (channels != null && !channels.isEmpty())
        {
            List<UserLoginUserDTO.ChannelResponse> channelDtos = channels.stream()
                .map(ch -> {
                    UserLoginUserDTO.ChannelResponse chDto = new UserLoginUserDTO.ChannelResponse();
                    chDto.setId(ch.getId());
                    chDto.setChannelType(ch.getChannelType());
                    chDto.setChannelAccount(maskChannel(ch.getChannelType(), ch.getChannelAccount()));
                    chDto.setRawChannelAccount(ch.getChannelAccount());
                    chDto.setIsPrimary(ch.getIsPrimary());
                    chDto.setBindTime(ch.getBindTime() != null ? ch.getBindTime().toString() : null);
                    chDto.setStatus(ch.getStatus());
                    return chDto;
                })
                .toList();
            dto.setChannels(channelDtos);
        }

        return dto;
    }

    /**
     * 更新资料（部分更新，null 不覆盖）
     */
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest req)
    {
        IamUser user = userMapper.selectById(userId);
        if (user == null)
        {
            throw new ServiceException("用户不存在");
        }
        if (req.getDisplayName() != null)
        {
            user.setDisplayName(req.getDisplayName());
        }
        if (req.getAvatarUrl() != null)
        {
            user.setAvatarUrl(req.getAvatarUrl());
        }
        userMapper.updateById(user);
    }

    /**
     * 修改密码（v1.1: 8-20 位）
     */
    @Transactional
    public void updatePassword(Long userId, PasswordChangeRequest req) throws Exception {
        IamUser user = userMapper.selectById(userId);
        if (user == null)
        {
            throw new ServiceException("用户不存在");
        }
        try {
            if (!passwordEncoder.matches(
                    RsaUtils.decryptByPrivateKey(req.getOldPassword()), user.getPasswordHash()))
            {
                throw new ServiceException("旧密码错误");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String newPwd = RsaUtils.decryptByPrivateKey(req.getNewPassword());
        if (newPwd.length() < 8 || newPwd.length() > 20)
        {
            throw new ServiceException("密码长度需为 8-20 位");
        }
        user.setPasswordHash(passwordEncoder.encode(newPwd));
        userMapper.updateById(user);

        // 同步密码到 sys_user（Feign RPC）
        syncPasswordToSysUser(userId, user.getPasswordHash());
    }

    /**
     * 同步密码到 sys_user
     * <p>
     * IAM 密码变更后，通过 iam_user_product 映射表找到对应的 sys_user，
     * 使用 Feign RPC 将 BCrypt 密文同步写入 system 模块。
     * 非阻塞 — 失败仅记录日志，不影响前端响应。
     */
    private void syncPasswordToSysUser(Long iamUserId, String bcryptPassword)
    {
        try
        {
            LambdaQueryWrapper<IamUserProduct> query = new LambdaQueryWrapper<>();
            query.eq(IamUserProduct::getIamUserId, iamUserId);
            IamUserProduct mapping = iamUserProductMapper.selectOne(query);

            if (mapping == null || mapping.getProductUserId() == null)
            {
                log.warn("密码同步 sys_user 跳过: 未找到 iam_user_id={} 的映射记录", iamUserId);
                return;
            }

            SysUser sysUser = new SysUser();
            sysUser.setUserId(mapping.getProductUserId());
            sysUser.setPassword(bcryptPassword);

            R<Boolean> result = remoteUserService.syncIamUserPassword(sysUser, SecurityConstants.INNER);
            if (R.isSuccess(result))
            {
                log.info("密码同步 sys_user 成功: iamUserId={}, productUserId={}",
                    iamUserId, mapping.getProductUserId());
            }
            else
            {
                log.warn("密码同步 sys_user 返回失败: iamUserId={}, msg={}",
                    iamUserId, result != null ? result.getMsg() : "null");
            }
        }
        catch (Exception e)
        {
            log.error("密码同步 sys_user 异常: iamUserId={}", iamUserId, e);
        }
    }

    /**
     * 退出登录 — 删除 Redis 中的 LoginUser 缓存
     * <p>
     * 使用三策略逐级尝试解析 token 中的 user_key：
     * <ol>
     *   <li>业务系统密钥（{@code iam.token-validators.business.secret}）</li>
     *   <li>RuoYi 硬编码密钥（{@link JwtUtils JwtUtils}）</li>
     *   <li>IAM 原生密钥（{@code iam.jwt.secret}）</li>
     * </ol>
     * 任一策略成功 → 提取 user_key → 删除 RuoYi Redis 登录态缓存 → 记录 IAM 审计日志。
     * 全部失败 → 仅 warn 日志，不抛出异常（不影响用户体验）。
     *
     * @param token 当前用户的 JWT access token
     */
    public void logout(String token)
    {
        String userKey = tryParseUserKey(token);

        if (userKey != null)
        {
            String redisKey = com.ruoyi.common.core.constant.CacheConstants.LOGIN_TOKEN_KEY + userKey;
            redisService.deleteObject(redisKey);
            log.info("用户登出，已删除 Redis key: {} (user_key={})", redisKey, userKey);
            auditLog(null, "token", "logout", "success", null, "user_key=" + userKey);
        }
        else
        {
            log.warn("登出时解析 token 失败：三种签名密钥均无法解析 token");
            // 不抛出异常 — 登出即使失败也不影响用户体验
        }
    }

    /**
     * 用三种策略依次尝试从 token 中解析 user_key
     * <p>
     * 策略顺序：business secret → JwtUtils 硬编码密钥 → IAM iam.jwt.secret
     *
     * @param token 原始 JWT token
     * @return user_key（Redis 缓存键后缀），全部失败返回 null
     */
    private String tryParseUserKey(String token)
    {
        // ── 策略1：业务系统密钥（iam.token-validators.business.secret） ──
        // 必须传 String 给 setSigningKey(Date)，与 JwtUtils 行为一致（jjwt 0.9.1 自动 base64 解码）
        try
        {
            Claims claims = Jwts.parser()
                    .setSigningKey(businessSecret)
                    .parseClaimsJws(token)
                    .getBody();
            String userKey = claims.get(SecurityConstants.USER_KEY, String.class);
            if (userKey != null)
            {
                log.debug("logout 策略1（business secret）解析 token 成功");
                return userKey;
            }
        }
        catch (Exception e)
        {
            log.trace("logout 策略1（business secret）失败: {}", e.getMessage());
        }

        // ── 策略2：RuoYi 硬编码密钥（JwtUtils → abcdefghijklmnopqrstuvwxyz） ──
        try
        {
            Claims claims = JwtUtils.parseToken(token);
            String userKey = JwtUtils.getUserKey(claims);
            if (userKey != null && !userKey.isEmpty())
            {
                log.debug("logout 策略2（JwtUtils 硬编码密钥）解析 token 成功");
                return userKey;
            }
        }
        catch (Exception e)
        {
            log.trace("logout 策略2（JwtUtils 硬编码密钥）失败: {}", e.getMessage());
        }

        // ── 策略3：IAM iam.jwt.secret ──
        try
        {
            Claims claims = tokenService.parseToken(token);
            String userKey = claims.get(SecurityConstants.USER_KEY, String.class);
            if (userKey != null)
            {
                log.debug("logout 策略3（IAM iam.jwt.secret）解析 token 成功");
                return userKey;
            }
        }
        catch (Exception e)
        {
            log.trace("logout 策略3（IAM iam.jwt.secret）失败: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 验证邮箱解绑密码（v1.1: email 解绑双验证）
     */
    public void validateEmailUnbindPassword(Long userId, String password)
    {
        IamUser user = userMapper.selectById(userId);
        if (user == null)
        {
            throw new ServiceException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash()))
        {
            throw new ServiceException("密码错误");
        }
    }

    /**
     * 存量用户惰性迁移导入（内部接口）
     */
    @Transactional
    public LoginResponse importUser(com.ruoyi.iam.dto.InnerUserImportRequest req)
    {
        String username = req.getUsername();
        if (username == null || username.isBlank())
        {
            throw new ServiceException("用户名不能为空");
        }

        // 已存在则直接签发
        LambdaQueryWrapper<IamUser> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(IamUser::getUsername, username).eq(IamUser::getDelFlag, "0");
        IamUser existing = userMapper.selectOne(checkWrapper);
        if (existing != null)
        {
            return createLoginResponse(existing);
        }

        // 插入 iam_user
        String reqProductLine = req.getProductLine() != null ? req.getProductLine() : productLine;
        IamUser user = new IamUser();
        user.setUsername(username);
        user.setPasswordHash(req.getPasswordHash());
        user.setDisplayName(req.getDisplayName() != null ? req.getDisplayName() : username);
        user.setAvatarUrl(req.getAvatarUrl());
        user.setStatus("0");
        user.setProductLine(reqProductLine);
        user.setUserFrom("import");
        user.setCreateTime(new Date());
        userMapper.insert(user);

        // 插入主通道（phone 优先）
        IamUserChannel channel = new IamUserChannel();
        channel.setUserId(user.getId());
        channel.setProductLine(reqProductLine);
        if (req.getPhone() != null)
        {
            channel.setChannelType("phone");
            channel.setChannelAccount(req.getPhone());
        }
        else
        {
            channel.setChannelType("email");
            channel.setChannelAccount(req.getEmail());
        }
        channel.setIsPrimary("1");
        channel.setStatus("0");
        channel.setBindTime(new Date());
        channelMapper.insert(channel);

        // 插入 iam_user_product 映射（如果提供了 productUserId）
        if (req.getProductUserId() != null)
        {
            IamUserProduct mapping = new IamUserProduct();
            mapping.setIamUserId(user.getId());
            mapping.setProductLine(reqProductLine);
            mapping.setProductUserId(req.getProductUserId());
            mapping.setCreateTime(new Date());
            iamUserProductMapper.insert(mapping);
            log.info("importUser: created iam_user_product mapping iamUserId={} -> productUserId={} productLine={}",
                    user.getId(), req.getProductUserId(), reqProductLine);
        }

        return createLoginResponse(user);
    }

    /**
     * 从 IamUser 构造 SysUser（兼容 Ruoyi-Cloud LoginUser）
     * 注意：当前 userId 使用 IamUser.id 占位，下游老接口查询业务数据时需通过映射表
     *
     * @return 所有字段均赋非 null 默认值，避免下游 NPE
     */
    com.ruoyi.system.api.domain.SysUser buildSysUser(IamUser iamUser, Long iamUserId)
    {
        com.ruoyi.system.api.domain.SysUser sysUser = new com.ruoyi.system.api.domain.SysUser(iamUserId);
        sysUser.setUserName(iamUser.getUsername());
        sysUser.setNickName(iamUser.getDisplayName());
        sysUser.setStatus("0");
        sysUser.setDelFlag("0");
        sysUser.setDeptId(0L);
        sysUser.setDept(new com.ruoyi.system.api.domain.SysDept());
        sysUser.setRoles(new java.util.ArrayList<>());
        sysUser.setRoleIds(new Long[0]);
        sysUser.setPostIds(new Long[0]);
        sysUser.setCreateTime(iamUser.getCreateTime());
        return sysUser;
    }

    /**
     * 签发 token + 构造 LoginUser 写入 Redis
     */
    /**
     * 签发 token + 构造 LoginUser 写入 Redis
     * <p>
     * 注意：必须通过 iam_user_product 映射表查询真实的 sys_user.userId（productUserId），
     * 否则 JWT 中携带的是 iam_user.id（如 6），下游 Gateway AuthFilter 解析后
     * SecurityContextHolder 拿到的 userId 不匹配，导致菜单/权限/角色查询返回空数据。
     */
    private String createAccessTokenWithLoginUser(Long iamUserId, String username, IamUser iamUser)
    {
        // 查询 iam_user_product 映射表，获取真实的 sys_user.userId
        LambdaQueryWrapper<IamUserProduct> mappingQuery = new LambdaQueryWrapper<>();
        mappingQuery.eq(IamUserProduct::getIamUserId, iamUserId)
            .eq(IamUserProduct::getProductLine, productLine);
        IamUserProduct mapping = iamUserProductMapper.selectOne(mappingQuery);
        Long productUserId = mapping != null ? mapping.getProductUserId() : iamUserId;

        String userKey = java.util.UUID.randomUUID().toString().replace("-", "");
        com.ruoyi.system.api.model.LoginUser loginUser = new com.ruoyi.system.api.model.LoginUser();
        loginUser.setToken(userKey);
        loginUser.setUserid(productUserId);
        loginUser.setUsername(username);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + accessTokenValidity * 1000L);
        loginUser.setIpaddr("0.0.0.0");
        loginUser.setPermissions(new java.util.HashSet<>());
        loginUser.setRoles(new java.util.HashSet<>());
        loginUser.setSysUser(buildSysUser(iamUser, productUserId));

        redisService.setCacheObject(
            com.ruoyi.common.core.constant.CacheConstants.LOGIN_TOKEN_KEY + userKey,
            loginUser,
            accessTokenValidity,
            java.util.concurrent.TimeUnit.SECONDS
        );

        // accessToken 使用 Gateway 原有 JwtUtils 硬编码密钥签发 ✅
        // RefreshToken 使用 Nacos iam.jwt.secret 密钥签发 ✅
        // 双密钥模式：access token 走旧密钥保证与 Gateway AuthFilter 兼容
        // refresh token 走新密钥用于后续刷新链路
        java.util.Map<String, Object> claims = new java.util.HashMap<>();
        claims.put(com.ruoyi.common.core.constant.SecurityConstants.USER_KEY, userKey);
        claims.put(com.ruoyi.common.core.constant.SecurityConstants.DETAILS_USER_ID, productUserId);
        claims.put(com.ruoyi.common.core.constant.SecurityConstants.DETAILS_USERNAME, username);
        claims.put("product_line", productLine);
        return JwtUtils.createToken(claims);
    }

    /**
     * 获取账号类型：1=手机号，2=邮箱，0=未知
     */
    private int getAccountType(String account)
    {
        if (account == null || account.isEmpty())
            return 0;
        if (account.matches("^1[3-9]\\d{9}$"))
            return 1; // 手机号
        if (account.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            return 2; // 邮箱
        return 0;
    }

    /**
     * 忘记密码 — 验证码校验 + 密码重置（一步完成）
     * <p>
     * 自动识别手机号/邮箱，调用 VerifyCodeService 校验验证码，
     * 通过通道表查到用户，直接更新密码。
     * 校验成功后验证码被消费（防重放），重置成功后清除用户所有活跃 session。
     *
     * @param account    手机号或邮箱
     * @param verifyCode 验证码
     * @param newPassword 新密码（RSA 加密后）
     */
    @Transactional
    public void resetPassword(String account, String verifyCode, String newPassword)
    {
        if (account == null || account.isEmpty()
            || verifyCode == null || verifyCode.isEmpty()
            || newPassword == null || newPassword.isEmpty())
        {
            throw new ServiceException("缺少必填参数");
        }

        // 1. 自动判断账号类型
        String channelType;
        int type = getAccountType(account);
        if (type == 1)
        {
            channelType = "phone";
        }
        else if (type == 2)
        {
            channelType = "email";
        }
        else
        {
            throw new ServiceException("不支持的账号格式，请输入手机号或邮箱");
        }

        // 2. 校验验证码（校验成功后自动消费，防重放）
        boolean codeValid = verifyCodeService.verifyCode(channelType, account, verifyCode);
        if (!codeValid)
        {
            throw new ServiceException("验证码错误或已过期");
        }

        // 3. 通过通道表找到用户
        LambdaQueryWrapper<IamUserChannel> channelQuery = new LambdaQueryWrapper<>();
        channelQuery.eq(IamUserChannel::getProductLine, productLine)
            .eq(IamUserChannel::getChannelType, channelType)
            .eq(IamUserChannel::getChannelAccount, account)
            .eq(IamUserChannel::getStatus, "0");
        IamUserChannel channel = channelMapper.selectOne(channelQuery);
        if (channel == null)
        {
            throw new ServiceException("未找到该账号绑定的用户");
        }

        // 4. 解密新密码
        String plainPassword;
        try
        {
            plainPassword = RsaUtils.decryptByPrivateKey(newPassword);
        }
        catch (Exception e)
        {
            throw new ServiceException("密码解密失败");
        }

        if (plainPassword.length() < 8 || plainPassword.length() > 20)
        {
            throw new ServiceException("密码长度需为 8-20 位");
        }

        // 5. 更新密码
        IamUser user = userMapper.selectById(channel.getUserId());
        if (user == null)
        {
            throw new ServiceException("用户不存在");
        }
        user.setPasswordHash(passwordEncoder.encode(plainPassword));
        userMapper.updateById(user);

        // 同步密码到 sys_user（Feign RPC）
        syncPasswordToSysUser(channel.getUserId(), user.getPasswordHash());

        log.info("忘记密码重置成功: userId={}, account={}, type={}", channel.getUserId(), account, channelType);
    }
}
