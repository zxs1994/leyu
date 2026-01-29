#!/bin/bash

echo "=============================="
echo "Deploy current directory JAR"
echo "=============================="

# 找到当前目录最新版本的 jar（按版本号排序，排除 original）
APP_NAME=$(ls -1 *.jar 2>/dev/null | grep -v original | sort -V | tail -n 1)

if [ -z "$APP_NAME" ]; then
  echo "❌ No jar file found in current directory"
  exit 1
fi

echo "✅ Found JAR: $APP_NAME"

# 查找正在运行的进程（匹配 leyu-admin 的任何版本）
pids=$(ps -ef | grep "leyu-admin.*jar" | grep -v grep | awk '{print $2}')

if [ -n "$pids" ]; then
  echo "🛑 Stopping old process..."
  for pid in $pids; do
    sudo kill -9 "$pid"
    echo "   Killed PID: $pid"
  done
else
  echo "ℹ️  No running process found."
fi

# 默认环境是 prod，如果脚本参数传了就用参数
ENV=${1:-prod}

echo "🚀 Starting new process in environment: $ENV"

sudo nohup java -jar "$APP_NAME" \
  --spring.profiles.active="$ENV" \
  > /dev/null 2>&1 &

echo "✅ Started $APP_NAME with profile $ENV"
echo "Done."