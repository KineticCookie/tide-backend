package models.db

import java.util.UUID

/**
  * Created by Bulat on 11.09.2016.
  */
// TODO Mist cluster IP-addr
case class User(id: UUID, fullname: String, email: String, pswd: String)
