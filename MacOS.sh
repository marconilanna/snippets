# Flush DNS cache
dscacheutil -flushcache



# Disable Notification Center
launchctl unload /System/Library/LaunchAgents/com.apple.notificationcenterui.plist
sudo mv /System/Library/LaunchAgents/com.apple.notificationcenterui.plist /System/Library/LaunchAgents/com.apple.notificationcenterui.bak
