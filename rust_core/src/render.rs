mod minimal_math_world;

use typst::layout::PagedDocument;
use thiserror::Error;
use minimal_math_world::MinimalMathWorld;

#[derive(Debug, Error, uniffi::Error)]
pub enum RenderingError {
  #[error("コンパイルエラー : {msg}")]
  CompileError { msg: String },
  
  #[error("内容が空です")]
  EmptyError,

  #[error("SVG 出力エラー")]
  SVGError,

  #[error("PNG 出力エラー")]
  PNGError
}

#[uniffi::export]
pub fn render_math_to_svg(math_code: String) -> Result<String, RenderingError> {
    // World の初期化
    let world = MinimalMathWorld::new(&math_code);

    // Typst コンパイラを実行して Document（内部表現）を生成
    let document: PagedDocument = typst::compile(&world)
        .output
        .map_err(|errs| RenderingError::CompileError { 
          msg: format!("コンパイルエラー: {:?}", errs) 
        })?;

    // 最初のページを取得して SVG 文字列に変換
    if let Some(page) = document.pages.first() {
        let raw_svg = typst_svg::svg(page);

        // usvg を使ってパース
        let opt = usvg::Options::default();
        let tree = usvg::Tree::from_str(&raw_svg, &opt)
            .map_err(|_| RenderingError::SVGError)?;

        // usvg のツリーから、単純化された SVG 文字列を再生成して出力
        let svg = tree.to_string(&usvg::WriteOptions::default());

        Ok(svg)
    } else {
        Err(RenderingError::EmptyError)
    }
}

#[uniffi::export]
pub fn render_math_to_png(math_code: String) -> Result<Vec<u8>, RenderingError> {
    // World の初期化
    let world = MinimalMathWorld::new(&math_code);

    // Typst コンパイラを実行して Document（内部表現）を生成
    let document: PagedDocument = typst::compile(&world)
        .output
        .map_err(|errs| RenderingError::CompileError { 
          msg: format!("コンパイルエラー: {:?}", errs) 
        })?;

    // 最初のページを取得して SVG 文字列に変換
    if let Some(page) = document.pages.first() {
        let pixmap = typst_render::render(page, 3.0);

        let bites = pixmap
            .encode_png()
            .map_err(|_| RenderingError::PNGError)?;

        Ok(bites)
    } else {
        Err(RenderingError::EmptyError)
    }
}