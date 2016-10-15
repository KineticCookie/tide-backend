package models.db

import java.util.UUID

/**
  * Created by Bulat on 03.10.2016.
  */
case class DataFlowLink(diagramId: UUID, from: UUID, to: UUID)
