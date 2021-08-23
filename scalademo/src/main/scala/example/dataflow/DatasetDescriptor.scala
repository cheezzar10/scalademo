package example.dataflow

case class DatasetDescriptor(
  name: String,
  inputColumns: IndexedSeq[String],
  outputColumns: IndexedSeq[String],
  // output column values expressed as inputColumns transformation chain
  translatedOutputColumns: IndexedSeq[String])
