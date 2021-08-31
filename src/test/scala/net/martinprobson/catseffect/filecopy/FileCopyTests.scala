package net.martinprobson.catseffect.filecopy

import cats.effect.{ExitCode, IO}
import cats.effect.testing.scalatest.AsyncIOSpec
import net.martinprobson.catseffect.filecopy.DomainError.{SourceAndDestinationTheSame, SourceFileDoesNotExist, WrongArgumentCount}
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, EitherValues}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.jdk.CollectionConverters._
import java.io.{ByteArrayOutputStream, File, FileWriter}

class FileCopyTests extends AsyncFlatSpec with AsyncIOSpec with Matchers with EitherValues with BeforeAndAfter with BeforeAndAfterAll {


    val fromDir: String = getClass.getClassLoader.getResource("d1").getPath
    val toDir: String = new File(fromDir).getParent + File.separator + "d3"
    val source: String = new File(fromDir).getParent + File.separator + "sourceFile"
    val destination: String = new File(fromDir).getParent + File.separator + "destinationFile"

    override def beforeAll(): Unit =  {
        FileUtils.deleteDirectory(new File(toDir))
        FileUtils.writeLines(new File(source), List("Hello","World").asJava)
        FileUtils.deleteQuietly(new File(destination))
    }

    override def afterAll(): Unit = {
        FileUtils.deleteDirectory(new File(toDir))
        FileUtils.deleteQuietly(new File(destination))
    }

    private def listFiles(directory: String): List[String] =
        FileUtils
                .listFilesAndDirs(new File(directory), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)
                .asScala
                .toList
                .map(_.getPath)

    "FileCopy " should "return WrongArgumentCount error when called with 3 arguments" in {
            FileCopy.runCopy(List("1", "2", "3")).asserting(_.left.value shouldBe WrongArgumentCount("Expected two arguments but got 3"))
    }

    it should "return WrongArgumentCount error when called with 1 arguement" in {
        FileCopy.runCopy(List("1")).asserting(_.left.value shouldBe WrongArgumentCount("Expected two arguments but got 1"))
    }

    it should "return WrongArgumentCount error when called with no arguments" in {
        FileCopy.runCopy(List()).asserting(_.left.value shouldBe WrongArgumentCount("Expected two arguments but got 0"))
    }

    it should "return SourceAndDestinationTheSame error when source and destination are the same" in {
        FileCopy.runCopy(List("1","1")).asserting(_.left.value shouldBe SourceAndDestinationTheSame)
    }

    it should "return SourceFileDoesNotExist if the source file is not found" in {
        FileCopy.runCopy(List("blah","desc")).asserting(_.left.value shouldBe SourceFileDoesNotExist)
    }

    it should "copy an entire directory tree to the destination" in {
        FileCopy.run(List(fromDir,toDir)).asserting(e => assert { e == ExitCode.Success &&
        listFiles(fromDir).sorted.map(_.replace("d1","d3")) ==
        listFiles(toDir).sorted
        })
    }

    it should "copy a single file" in {
        FileCopy.run(List(source, destination)).asserting(e => assert {
            e == ExitCode.Success &&
                    FileUtils.readFileToString(new File(source),"UTF-8") ==
                            FileUtils.readFileToString(new File(destination), "UTF-8")
        })
    }
}
