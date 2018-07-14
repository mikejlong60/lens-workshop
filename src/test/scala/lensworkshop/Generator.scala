package lensworkshop

import java.util.Date

import lensworkshop.model.{UserGroup, UserGroupKey, Users}
import org.scalacheck.Gen

import scala.concurrent.duration._

object Generator {

  def genUserGroup: Gen[UserGroup] = {
    for {
      startTime <- Gen.oneOf((1 to 365).map(x => new Date(System.currentTimeMillis() + (x days).toMillis).getTime))
      groupName <- Gen.alphaStr
      projectName <- Gen.alphaStr
      userTokens <- Gen.listOf(Gen.alphaStr)
      timeToLive <- Gen.oneOf(10000 to 1000000)
    } yield (
      UserGroup(
        UserGroupKey(projectName = projectName, groupName = groupName),
        value = Users(startTime = startTime, timeToLive = timeToLive, userTokens = userTokens)
      )
    )
  }

  def genUserGroupMap: Gen[(Map[UserGroupKey, Users], UserGroup)] = {
    for {
      groups <- Gen.nonEmptyListOf(genUserGroup)
      group <- Gen.oneOf(groups)
    } yield (groups.foldLeft(Map.empty[UserGroupKey, Users])((z, g) => z + (g.key -> g.value)), group)
  }

  def genOverlappingUserGroupMaps: Gen[(Map[UserGroupKey, Users], Map[UserGroupKey, Users], UserGroupKey)] = {
    for {
      all <- genUserGroupMap
      map1 <- Gen.pick(all._1.size / 3, all._1)
      map2 <- Gen.pick(all._1.size / 2, all._1)
      if (map1.size > 0 && map2.size > 0)
      groupName <- Gen.alphaStr
      projectName <- Gen.alphaStr
      key <- Gen.oneOf(map1.head._1, map2.head._1, UserGroupKey(groupName = groupName, projectName = projectName)
      )
    } yield (map1.toMap, map2.toMap, key)
  }

}
