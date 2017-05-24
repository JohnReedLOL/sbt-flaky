package flaky.history

import java.io.File

import flaky.Unzip
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.util.Success

class GitSpec extends WordSpec with Matchers with BeforeAndAfterAll with Unzip {
  private val zipped = new File("./src/test/resources", "gitrepo.zip")
  private val unzipDir = new File("target/")
  private val git = Git(new File(unzipDir, "gitrepo"))

  "Git " should {
    "list all changes" in {
      val expected: Seq[GitCommit] = List(
        "549df19", //Commit 1
        "a220598", //Commit 2
        "d4fa72b", //Commit 3
        "a9958e5", //Commit 4
        "e5677bb", //Commit 5
        "2939f3a", //Commit 6
        "b55953a", //Commit 7
        "9d74e32" //Commit 8
      )
        .zipWithIndex
        .map(a => GitCommit(a._1, "user@email.com", s"Commit ${a._2 + 1}"))
        .reverse

      git.history() shouldBe Success(expected)
    }
    "find commits list between 2 hashes" in {
      val commitsList = git.commitsList("d4fa72b", "2939f3a")
      //4,5,6
      commitsList shouldBe Success(List(
        GitCommit("2939f3a", "user@email.com", "Commit 6"),
        GitCommit("e5677bb", "user@email.com", "Commit 5"),
        GitCommit("a9958e5", "user@email.com", "Commit 4")
      ))
    }
    "find current commit" in {
      git.currentId() shouldBe Success("9d74e32")
    }
  }

  override protected def beforeAll(): Unit = {
    unzip(zipped, unzipDir)
  }
}