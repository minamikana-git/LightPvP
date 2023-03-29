# ⚔ LightPvP ⚔

[![forthebadge made-with-java](https://forthebadge.com/images/badges/made-with-java.svg)](https://java.com/)

らいくら鯖のイベント用に開発されたプラグイン


## 📚 機能

- プレイヤーを指定して自動的にシードの選出や対戦表の作成を行う

- MinecraftのMapとして対戦表を描画する

- 試合が開始された時、対戦するプレイヤーをフィールドへテレポートする


## 💬 使用方法

### コマンド一覧
```
/lt register [MCID]: 参加者を登録する(サーバーに接続している人のみ登録可能)
/lt unregister [MCID]: 参加者の登録を解除する
/lt create: トーナメントを構築する
/lt terminate: トーナメントを強制的に終了する
/lt next: 次の試合を開始する
/lt list: 登録済みプレイヤーを表示する
/lt info: トーナメントの情報を表示する
/lt set [lobby/left/right]: トーナメントに使用する座標を設定する
/lt clear-frames: 設定済みの額縁を削除する
```

### 額縁の登録

トーナメント表を表示するために額縁を登録する必要があります

OP権限を持ったプレイヤーがブレイズロッドを持って輝く額縁を左クリックすることで設定可能です

縦5x横6ブロックの額縁を左上から右下にかけて30個クリックしてください

```
[1 ][2 ][3 ][4 ][5 ]
[6 ][7 ][8 ][9 ][10]
[11][12][13][14][15]
[16][17][18][19][20]
[21][22][23][24][25]
[26][27][28][29][30]
```


## 🖋 備考

- 座標の情報と額縁の情報は再起動後も保持されますが参加者の情報は再起動後には保持されません

- プラグインのデータフォルダ内に「leaderboard.png」もしくは「leaderboard.ttf」という名前のファイルを入れることでトーナメント表の背景画像やフォントを差し替えることが出来ます
画像を差し替える場合は必ず縦640px横768pxとなるように調整してください
```
plugins
  └── LightPvP
        ├── leaderboard.png ... (*)
        ├── leaderboard.ttf ... (*)
        └── config.yml
```


## Thanks to

Jorel Ali for [CommandAPI](https://github.com/JorelAli/CommandAPI)

The Project Lombok Authors for [Lombok](https://github.com/projectlombok/lombok)
