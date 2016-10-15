package models

import java.util.UUID

/**
  * Created by Bulat on 13.09.2016.
  */
case class LoginSession(user_id: UUID, token: UUID)
