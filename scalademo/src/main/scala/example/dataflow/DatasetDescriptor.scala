package example.dataflow

case class DatasetDescriptor(
  name: String,
  columns: IndexedSeq[String],
  // column values expressed as columns transformation chain
  translatedColumns: IndexedSeq[String])
