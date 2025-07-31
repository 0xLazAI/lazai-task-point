REPO_URL="git@github.com:your-org/your-springboot-project.git"
PROJECT_DIR="springboot-app"
BRANCH="main"

REMOTE_PATH="/root/webserver/gaia"
SERVERS=(
  "root@192.168.1.10"
)
SSH_KEY="~/.ssh/id_rsa"
PORT=22
APP_JAR_NAME="your-app-name.jar"

# ========================
# 开始部署
# ========================

for SERVER in "${SERVERS[@]}"; do
  echo "🚀 部署到 $SERVER"

  ssh -i $SSH_KEY -p $PORT $SERVER <<EOF
    set -e
    echo "📁 准备目录：$REMOTE_PATH"
    mkdir -p $REMOTE_PATH
    cd $REMOTE_PATH

    # 克隆或拉取最新代码
    if [ ! -d "$PROJECT_DIR" ]; then
      echo "🔽 clone 仓库"
      git clone -b $BRANCH $REPO_URL $PROJECT_DIR
    else
      echo "🔁 pull 最新代码"
      cd $PROJECT_DIR
      git fetch origin
      git reset --hard origin/$BRANCH
    fi

    cd $PROJECT_DIR

    # 构建并运行
    echo "🔨 编译项目"
    ./mvnw clean package -DskipTests

    echo "🧹 杀掉旧进程"
    PID=\$(ps -ef | grep $APP_JAR_NAME | grep -v grep | awk '{print \$2}')
    if [ ! -z "\$PID" ]; then
      kill -9 \$PID
    fi

    echo "🚀 启动 Spring Boot 应用"
    nohup java -jar target/$APP_JAR_NAME > logs.log 2>&1 &
    echo "✅ 部署完成"
EOF

done

echo "🎉 所有节点部署成功"