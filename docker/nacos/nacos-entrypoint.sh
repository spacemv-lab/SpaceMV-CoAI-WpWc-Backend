#!/bin/bash
#
# Nacos 启动安全校验脚本
#
# 功能：
# 1. 检查 NACOS_AUTH_TOKEN_SECRET_KEY 是否设置且不为空
# 2. 检查是否使用了默认值
# 3. 只有所有校验通过后才启动 Nacos
#
# 使用方法：
#   通过 dockerfile ENTRYPOINT 自动调用

set -e

# 从环境变量读取（如果通过 docker-compose environment 传入）
TOKEN_KEY="${NACOS_AUTH_TOKEN_SECRET_KEY}"

# 校验：是否为空
if [ -z "${TOKEN_KEY}" ]; then
    echo ""
    echo "================================================"
    echo "  [安全校验失败] NACOS_AUTH_TOKEN_SECRET_KEY 未设置！"
    echo ""
    echo "  请在 docker-compose.yml 或启动命令中设置环境变量："
    echo "    NACOS_AUTH_TOKEN_SECRET_KEY=你的Base64密钥"
    echo ""
    echo "  密钥生成命令："
    echo "    openssl rand -base64 32"
    echo "================================================"
    echo ""
    exit 1
fi

# 校验：是否使用了默认/弱密钥
if [ "${TOKEN_KEY}" = "SecretKey012345678901234567890123456789012345678901234567890123456789" ] || \
   [ "${TOKEN_KEY}" = "nacos" ] || \
   [ "${TOKEN_KEY}" = "nacos_default" ]; then
    echo ""
    echo "================================================"
    echo "  [安全校验失败] NACOS_AUTH_TOKEN_SECRET_KEY 使用了默认/弱密钥！"
    echo ""
    echo "  检测到密钥：${TOKEN_KEY}"
    echo "  请修改为强密钥，建议使用："
    echo "    openssl rand -base64 32"
    echo "================================================"
    echo ""
    exit 1
fi

echo "[安全校验通过] NACOS_AUTH_TOKEN_SECRET_KEY 已设置且有效"

# 校验身份标识（非强制，但发出警告）
IDENTITY_KEY="${NACOS_AUTH_SERVER_IDENTITY_KEY:-your-custom-identity-key}"
IDENTITY_VALUE="${NACOS_AUTH_SERVER_IDENTITY_VALUE:-your-custom-identity-value}"

if [ "${IDENTITY_KEY}" = "your-custom-identity-key" ] || [ "${IDENTITY_VALUE}" = "your-custom-identity-value" ]; then
    echo ""
    echo "================================================"
    echo "  [安全提示] NACOS_AUTH_SERVER_IDENTITY_KEY/VALUE 使用了默认值"
    echo ""
    echo "  建议自定义身份标识以防止跨服务请求伪造，"
    echo "  在 docker-compose.yml 中设置："
    echo "    NACOS_AUTH_SERVER_IDENTITY_KEY=your-custom-key"
    echo "    NACOS_AUTH_SERVER_IDENTITY_VALUE=your-custom-value"
    echo "================================================"
    echo ""
fi

# 所有校验通过，启动 Nacos
exec /bin/bash /home/nacos/bin/docker-startup.sh
