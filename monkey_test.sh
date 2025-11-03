#!/usr/bin/env bash
# File: `run_monkey.sh`
# Usage: ./run_monkey.sh [iterations] [throttle_seconds] [package] [activity]
ITERATIONS=${1:-1000}
THROTTLE=${2:-0.15}
PACKAGE=${3:-com.uniandes.medisupply}
ACTIVITY=${4:-.MainActivity}

# build component string (package/activity)
if [[ "$ACTIVITY" == *"/"* ]]; then
  COMPONENT="$ACTIVITY"
elif [[ "$ACTIVITY" == .* ]]; then
  COMPONENT="${PACKAGE}${ACTIVITY}"
elif [[ "$ACTIVITY" == *.* ]]; then
  COMPONENT="${PACKAGE}/${ACTIVITY}"
else
  COMPONENT="${PACKAGE}/.${ACTIVITY}"
fi

echo "Starting activity: $COMPONENT"
adb shell am start -n "$COMPONENT" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER >/dev/null 2>&1
sleep 1

# Get device screen size (format: WIDTHxHEIGHT)
SIZE=$(adb shell wm size | awk -F'[: ]+' '{print $NF}' | tr -d '\r')
if [[ -z "$SIZE" ]]; then
  echo "No device found or failed to get screen size."
  exit 1
fi
WIDTH=${SIZE%x*}
HEIGHT=${SIZE#*x}

echo "Device size: ${WIDTH}x${HEIGHT} - Iterations: $ITERATIONS - Throttle: ${THROTTLE}s"

for i in $(seq 1 $ITERATIONS); do
  X=$((RANDOM % WIDTH))
  Y=$((RANDOM % HEIGHT))
  adb shell input tap $X $Y
  sleep $THROTTLE
done

echo "Done."
