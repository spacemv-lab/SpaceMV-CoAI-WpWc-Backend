package com.ruoyi.system.controller.internal;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.system.api.domain.SysNoticeVO;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通知公告内部控制器
 * 仅供微服务间内部调用，通过 @InnerAuth 保护
 *
 * @author txwx
 */
@RestController
@RequestMapping("/internal/v1/notice")
public class NoticeInternalController extends BaseController {

    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 新增系统同步通知（类型3）
     */
    @PostMapping("/system")
    @InnerAuth
    public R<Integer> insertSystemNotice(@RequestBody SysNoticeVO vo) {
         int rows = noticeService.insertNotice(convertToDomain(vo));
        return R.ok(rows);
    }

    /**
     * 新增用户操作通知（类型4）
     */
    @PostMapping("/user-oper")
    @InnerAuth
    public R<Integer> insertUserOperNotice(@RequestBody SysNoticeVO vo) {
        int rows = noticeService.insertNotice(convertToDomain(vo));
        return R.ok(rows);
    }

    /**
     * VO 转 Domain
     */
    private SysNotice convertToDomain(SysNoticeVO vo) {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(vo.getNoticeId());
        notice.setNoticeTitle(vo.getNoticeTitle());
        notice.setNoticeType(vo.getNoticeType());
        notice.setNoticeContent(vo.getNoticeContent());
        notice.setStatus(vo.getStatus());
        notice.setCreateBy(vo.getCreateBy());
        notice.setRemark(vo.getRemark());
        return notice;
    }
}
