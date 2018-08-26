package lensworkshop.lense.lenscomposition

import lensworkshop.Generator
import lensworkshop.lense.Lens
import lensworkshop.lense.cache.{UserGroup, Users}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, _}

class DeepCopyLensTest extends PropSpec with PropertyChecks with Matchers {

  val userGroupUsersLens = Lens[UserGroup[String], Users[String]](a => a.value, (b, a) => a.copy(value = b))
  val usersUserTokensLens = Lens[Users[String], List[String]](a => a.userTokens, (b, a) => a.copy(userTokens = b))

  //Now compose Lenses to make a lens that will replace the UserGroup.Users.userTokens field
  val userGroupUsersUserTokensLens: Lens[UserGroup[String], List[String]] = userGroupUsersLens andThen usersUserTokensLens //(notificationEventLens).andThen(eventSourceLens)

  property("should allow deep copy just like how you navigate" +
    "into a mutable structure to change a field in an imperative language " +
    "like Java or Javascript.") {
    forAll(Generator.genUserGroup) { whole: UserGroup[String] =>

      Gen.listOf(Gen.alphaStr).sample.map(part => {
        val newWhole = userGroupUsersUserTokensLens.set(part, whole)
        println(part)
        newWhole.value.userTokens should be(part)
      })
    }
  }

  property("Set after get rule") {
    forAll(Generator.genUserGroup) { whole: UserGroup[String] =>
      Gen.listOf(Gen.alphaStr).sample.map { part =>
        val newWhole = userGroupUsersUserTokensLens.set(userGroupUsersUserTokensLens.get(whole), whole)
        newWhole should be(whole)
      }
    }
  }

  property("get after set rule") {
    forAll(Generator.genUserGroup) { whole: UserGroup[String] =>
      Gen.listOf(Gen.alphaStr).sample.map { part =>
        val newPart = userGroupUsersUserTokensLens.get(userGroupUsersUserTokensLens.set(part, whole))
        newPart should be(part)
      }
    }
  }

  property("set after set rule") {
    forAll(Generator.genUserGroup) { whole: UserGroup[String] =>
      Gen.listOf(Gen.alphaStr).sample.map { part =>
        val setAfterSetWhole = userGroupUsersUserTokensLens.set(part, userGroupUsersUserTokensLens.set(part, whole))
        val setWhole = userGroupUsersUserTokensLens.set(part, whole)
        setAfterSetWhole should be(setWhole)
      }
    }
  }
}
