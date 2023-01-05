# IndigoMotd
BungeeCord MOTD plugin with random icons and text support

# Screenshots
![Screenshot_1](https://user-images.githubusercontent.com/78136575/210823261-834f5e8e-9209-4ebd-b88e-ffd7c301579e.png)
![Hoverbox](https://user-images.githubusercontent.com/78136575/210823403-f80df8b3-190e-4258-bd6f-5562353f92ae.png)
![Screenshot_2](https://user-images.githubusercontent.com/78136575/210823651-6b37f634-1be9-415a-ad03-03ebd92e678c.png)

# Addition information
This plugin is no longer in development, sorry
This plugin use bStats, you can check plugin page [here](https://bstats.org/plugin/bungeecord/IndigoMOTD-Bungee/16082)

# config.yml
```yml
enable-players: true
motd:
  lineOne:
    - "&9&lExample line 1"
    - "&e&lExample line 2"
  lineTwo:
    - "&c&lExample next line 1"
    - "&b&lExample next line 2"
  split-random: false #Use fifferent random for lines?
protocol-ver: 758 #1.18.2, see minecraft wiki for other
protocol-name: "&9&lJOIN &e-->>                                             &7%online%&8/&7%max%"
random-images:
  - "icon1.png"
  - "icon2.png"
  - "icon3.png"
scan-all-icons: false #Scan `icons` folder for icons or use icons specified here
change:
  online: true #use players.fake-online?
  max-online: true #use fake-max-online?
  players: true #Enable hover text?
  icon: true #change server icon?
  protocol: true #use `protocol-ver` and `protocol-name`
  motd: true #change MOTD?
  protocol-name: true #change protocol name? Displayed when `protocol-ver` is not same as client protocol version
  protocol-ver: true #change protocol version?
players:
  fake-max-online: 100
  fake-online: 99
  online: "%max%-1" #here you can use math expressions
  max: "%fake%" #here you can use math expressions
  top: #text in hoverbox
    - "&bCurrent Online: %online%/%max%"
    - ""
  entry: "&9%name%" #text in hoverbox after `players.top`
  playersServer: "GLOBAL" #from what server display players to hoverbox? `GLOBAL` is all servers
  displayNames: false #append entries in hoverbox after `players.top`?
  ```
