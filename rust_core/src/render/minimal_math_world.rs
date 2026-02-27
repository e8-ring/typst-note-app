use typst::diag::{FileError, FileResult};
use typst::foundations::{Bytes, Datetime};
use typst::syntax::{FileId, Source, VirtualPath};
use typst::text::{Font, FontBook};
use typst::{Library, LibraryExt};
use typst::utils::LazyHash;

// World を表現する構造体を定義
pub struct MinimalMathWorld {
  library: LazyHash<Library>,
  book: LazyHash<FontBook>,
  fonts: Vec<Font>,
  source: Source,
}

impl MinimalMathWorld {
  pub fn new(math_code: &str) -> Self {
    // 標準ライブラリを構築（Typst の組み込み関数など）
    let library = LazyHash::new(Library::default());

    // --- フォントの準備 ---
    // ここが重要。数式を綺麗に描画するには数学用フォントが必要。
    // （例：NewCMMath-Regular.otf などをプロジェクトの assets フォルダに入れておく）
    // include_bytes! マクロを使うと、コンパイル時にバイナリとしてアプリに埋め込まれます。
    let font_bytes = include_bytes!("../../assets/NewCMMath-Regular.otf");
    let font = Font::new(Bytes::new(font_bytes), 0)
        .expect("フォントの読み込みに失敗しました");
    
    let fonts = vec![font];

    // フォントブック（フォントの目録）を作成
    let mut book = FontBook::new();
    for font in &fonts {
        book.push(font.info().clone());
    }
    let book = LazyHash::new(book);

    // --- ソースコードの準備 ---
    // Flutter から受け取った数式コードを Typst の書式でラップします
    let typst_source = format!("{}", math_code);

    // 仮のファイル名を与えて Source オブジェクトを作成
    let file_id = FileId::new(None, VirtualPath::new("main.typ"));
    let source = Source::new(file_id, typst_source);

    Self {
        library,
        book,
        fonts,
        source,
    }
  }
}

impl typst::World for MinimalMathWorld {
    fn library(&self) ->  &LazyHash<Library> {
        &self.library
    }

    fn book(&self) -> &LazyHash<FontBook> {
        &self.book
    }

    fn main(&self) -> FileId {
        self.source.id()
    }

    fn source(&self, id: FileId) -> FileResult<Source> {
        // メインファイルだけは返し、それ以外の import などは全て「見つからない」として弾く
        if id == self.source.id() {
            Ok(self.source.clone())
        } else {
            Err(FileError::NotFound(id.vpath().as_rootless_path().into()))
        }
    }

    fn file(&self, id: FileId) -> FileResult<Bytes> {
        // 画像などの外部ファイル読み込みも非対応として弾く
        Err(FileError::NotFound(id.vpath().as_rootless_path().into()))
    }

    fn font(&self, index: usize) -> Option<Font> {
        // 要求されたインデックスのフォントを返す
        self.fonts.get(index).cloned()
    }

    fn today(&self, _offset: Option<i64>) -> Option<Datetime> {
        // 数式の描画に日付は不要なので None で OK
        None
    }
}