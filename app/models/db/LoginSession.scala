package models.db

import java.time.LocalDateTime
import java.util.UUID

/**
  * Created by Bulat on 13.09.2016.
  */
case class LoginSession(user_id: UUID, token: UUID, expires: LocalDateTime)
