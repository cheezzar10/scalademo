package example.dataflow

case class Pipeline(val stages: IndexedSeq[Transformer])
