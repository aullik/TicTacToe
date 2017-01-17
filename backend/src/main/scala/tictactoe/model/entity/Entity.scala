package tictactoe.model.entity

/**
  * An Entity inside of the Database.
  * <p>
  * Every class extending Entity will be saved in the Database and can be loaded via [[tictactoe-model.entity.Entity#id]]
  *
  */
trait Entity[ID <: EntityId] extends Product {

  val id: ID

}

object Entity {
}
