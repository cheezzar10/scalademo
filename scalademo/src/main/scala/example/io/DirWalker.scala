package example.io

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.collection.mutable.ArrayBuffer
import scala.sys.process.Process

object DirWalker {
  private val BuildSbtFileName: String = "build.sbt"
  private val VersionFileName: String = "version.properties"

  def main(args: Array[String]): Unit = {
    val rootDirPath = Paths.get(sys.props("user.home"), "Documents/onefactor/ml-playground")

    val fileSearcher = new FileSearchVisitor(rootDirPath, BuildSbtFileName)
    Files.walkFileTree(rootDirPath, fileSearcher)

    val projectDirPaths = fileSearcher.foundFiles
      .map(_.getParent)
      .filter(_ != null)

    for (foundFile <- projectDirPaths) {
      println(s"found project directory: '$foundFile'")
    }

    val changedFiles =
      Process(s"git --git-dir ${rootDirPath}/.git log --format= --no-merges  --name-status master..HEAD")
        .lineStream
        .map(line => Paths.get(line.split("\t")(1)))
        .toIndexedSeq

    val changedProjects = changedFiles
      .map { changedFile =>
        projectDirPaths.find { projectDirPath =>
          changedFile.startsWith(projectDirPath)
        }
      }
      .flatten
      .toSet

    for (changedProject <- changedProjects) {
      println(s"changed project: $changedProject")
    }
  }
}

class FileSearchVisitor(rootPath: Path, fileName: String) extends SimpleFileVisitor[Path] {
  private val foundFilesBuf = ArrayBuffer.empty[Path]

  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
    if (file.getFileName.toString == fileName) {
      val parents = rootPath.relativize(file.getParent)
        .iterator
        .asScala
        .map(_.toString)

      if (!parents.contains("target")) {
        foundFilesBuf += rootPath.relativize(file)
      }
    }

    FileVisitResult.CONTINUE
  }

  def foundFiles(): IndexedSeq[Path] = {
    foundFilesBuf.toIndexedSeq
  }
}
