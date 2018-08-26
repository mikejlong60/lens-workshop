package lensworkshop.lense.cache

sealed trait AbstractUser[+A]

case class Users[A](startTime: Long, timeToLive: Long, userTokens: List[A]) extends AbstractUser[A]

case class UserGroupKey[A](projectName: A, groupName: A) extends AbstractUser[A]

case class UserGroup[A](key:UserGroupKey[A], value: Users[A]) extends AbstractUser[A]


