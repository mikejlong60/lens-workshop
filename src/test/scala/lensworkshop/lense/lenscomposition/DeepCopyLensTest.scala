package lensworkshop.lense.lenscomposition

import lensworkshop.Generator
import lensworkshop.lense.Lens
import lensworkshop.lense.cache.{InMemoryCache, UserGroup, UserGroupKey, Users}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, _}

class DeepCopyLensTest extends PropSpec with PropertyChecks with Matchers {

  val userGroupUsersLens = Lens[UserGroup[String], Users[String]](a => ???, (b, a) => ???)
  val usersUserTokensLens = Lens[Users[String], List[String]](a => ???, (b, a) => ???)

  //Now compose Lenses to make a lens that will replace the UserGroup.Users.userTokens field
  val userGroupUsersUserTokensLens: Lens[UserGroup[String], List[String]] = userGroupUsersLens andThen usersUserTokensLens

  property("horrid nested copy problem when you need to add a new element") {
    forAll(Generator.genUserGroup) { whole   =>

      val key  = whole.key
      val users = whole.value
      val newUser = "mike"
      val part = newUser +: users.userTokens

      //Imperative approach -- It gets much worse as a structure has more layers.
      val newUsers =   users.copy(userTokens = part)
      val newWholeImperitve = UserGroup(key = key, value = newUsers)

      //Lens approach
      val newWhole = userGroupUsersUserTokensLens.set(part, whole)
      newWhole shouldBe (newWholeImperitve)

    }

  }

  property("should allow deep copy just like how you navigate" +
    "into a mutable structure to change a field in an imperative language " +
    "like Java or Javascript.") {
    forAll(Generator.genUserGroup) { whole: UserGroup[String] =>

      Gen.listOf(Gen.alphaStr).sample.map(part => {
        val newWhole = userGroupUsersUserTokensLens.set(part, whole)
        newWhole.value.userTokens should be(part)
      })
    }
  }


  //Set-Get – If you modify something by inserting a particular sub-part,  then you get back exactly what you set.
  property("Set after get rule") {
    forAll(Generator.genUserGroup) { whole: UserGroup[String] =>
      Gen.listOf(Gen.alphaStr).sample.map { part =>
        val newWhole = userGroupUsersUserTokensLens.set(userGroupUsersUserTokensLens.get(whole), whole)
        newWhole should be(whole)
      }
    }
  }

  //Get-Set – If you modify something by changing its sub-part to exactly what it was before, then nothing happens.
  property("Get after set rule") {
    forAll(Generator.genUserGroup) { whole: UserGroup[String] =>
      Gen.listOf(Gen.alphaStr).sample.map { part =>
        val newPart = userGroupUsersUserTokensLens.get(userGroupUsersUserTokensLens.set(part, whole))
        newPart should be(part)
      }
    }
  }

  //Set-Set – If you modify something by inserting a particular sub-part and then modify it again,  its exactly as if you only made the second modification.
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
