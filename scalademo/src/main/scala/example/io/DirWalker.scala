package example.io

import java.nio.charset.StandardCharsets
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.sys.process.Process
import scala.util.matching.Regex

object DirWalker {
  private val BuildSbtFileName: String = "build.sbt"
  private val VersionFileName: String = "version.properties"
  private val ModuleVersionPattern: Regex = """\w+\.version=(\d+)\.(\d+)\.(\d+)""".r

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

    val manuallyChangedVersionFiles = (changedFiles
      .filter(path => path.getFileName.toString == VersionFileName)
      .toSet + Paths.get("tests/version.properties")) - Paths.get("mld/batch/version.properties")

    val versionFilesToChange = changedFiles
      .map { changedFile =>
        projectDirPaths.find { projectDirPath =>
          changedFile.startsWith(projectDirPath)
        }
      }
      .flatten
      .map(_.resolve(VersionFileName))
      .toSet
      .diff(manuallyChangedVersionFiles)

    println("----- manually changed version files -----")
    for (manuallyChangedVersionFile <- manuallyChangedVersionFiles) {
      println("manually changed version file: " + manuallyChangedVersionFile)
    }
    println("----- manually changed version files -----")

    println("----- version.properties files to change -----")
    for (versionFileToChange <- versionFilesToChange) {
      println(s"changed project: $versionFileToChange")

      val versionFileAbsolutePath = rootDirPath.resolve(versionFileToChange)
      val versionFileContent =
        new String(Files.readAllBytes(versionFileAbsolutePath), StandardCharsets.UTF_8).trim

      println("version file content: '" + versionFileContent + "'")
      versionFileContent match {
        case ModuleVersionPattern(major, minor, bugfix) =>
          println("minor version: " + minor)

          val incrementedMinorVersion = minor.toInt + 1
          val updatedVersionFileContent = versionFileContent
            .replace(s"$major.$minor.$bugfix", s"$major.$incrementedMinorVersion.$bugfix")

          println("updated version file content: '" + updatedVersionFileContent + "'")
          Files.write(versionFileAbsolutePath, Seq(updatedVersionFileContent).asJava)
      }
    }
    println("----- version.properties files to change -----")
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
