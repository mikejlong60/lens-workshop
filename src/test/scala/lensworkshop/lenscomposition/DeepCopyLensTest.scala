package lensworkshop.lenscomposition

import lensworkshop.cache.{Generator, Lens}
import lensworkshop.model.{UserGroup, Users}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, _}

class DeepCopyLensTest extends PropSpec with PropertyChecks with Matchers {

  val userGroupUsersLens = Lens[UserGroup, Users](a => a.value, (b, a) => a.copy(value = b))
  val usersUserTokensLens = Lens[Users, List[String]](a => a.userTokens, (b, a) => a.copy(userTokens = b))

  //Now compose Lenses to make a lens that will replace the UserGroup.Users.userTokens field
  val userGroupUsersUserTokensLens: Lens[UserGroup, List[String]] = userGroupUsersLens andThen usersUserTokensLens //(notificationEventLens).andThen(eventSourceLens)

  property("should allow deep copy just like how you navigate" +
    "into a mutable structure to change a field in an imperative language " +
    "like Java or Javascript.") {
    forAll(Generator.genUserGroup) { userGroup: UserGroup =>

      Gen.listOf(Gen.alphaStr).sample.map(newUserTokens => {
        val groupWithNewUserTokens = userGroupUsersUserTokensLens.set(newUserTokens, userGroup)
        println(newUserTokens)
        groupWithNewUserTokens.value.userTokens should be(newUserTokens)
      })
    }
  }
}
