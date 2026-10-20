"use strict";

async function startMaze() {
  const status = document.getElementById("status");
  try {
    if (location.protocol === "file:") {
      throw new Error("HTMLを直接開く方法では起動できません。ローカルサーバーまたは公開URLから開いてください。");
    }
    status.textContent = "Java実行環境を読み込み中です。初回は時間がかかる場合があります。";
    await new Promise((resolve, reject) => {
      const loader = document.createElement("script");
      loader.src = "https://cjrtnc.leaningtech.com/4.3/loader.js";
      loader.onload = resolve;
      loader.onerror = () => reject(new Error("CheerpJを読み込めません。インターネット接続を確認してください。"));
      document.head.appendChild(loader);
    });
    await cheerpjInit({ version: 17 });
    cheerpjCreateDisplay(-1, -1, document.getElementById("display"));
    // /appはCheerpJの仮想パス。サブフォルダでの公開にも対応する。
    const jarUrl = new URL("./maze.jar", document.baseURI);
    status.textContent = "迷路アプリを起動します。画面が表示されたら設定を選んで開始してください。";
    const exitCode = await cheerpjRunJar("/app" + jarUrl.pathname);
    if (typeof exitCode === "number" && exitCode !== 0) {
      throw new Error("Javaアプリの終了コード：" + exitCode);
    }
  } catch (error) {
    console.error(error);
    status.textContent = "起動エラー：" + error.message;
  }
}

startMaze();
