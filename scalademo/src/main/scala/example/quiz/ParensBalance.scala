package example.quiz

object ParensBalance {
  def main(args: Array[String]): Unit = {
    val str = "]"
    println(s"'$str' is balanced: ${isBalanced(str)}")
  }

  private def isBalanced(str: String) = {
    def loop(chars: List[Char], stack: List[Char]): Boolean = chars match {
      case chr :: tail => chr match {
        case '(' => loop(tail, ')' :: stack)
        case '{' => loop(tail, '}' :: stack)
        case '[' => loop(tail, ']' :: stack)
        case ')' | '}' | ']' => stack match {
          case top :: bottom => if (top == chr) loop(tail, bottom) else false
          case Nil => false
        }
        case _ => loop(tail, stack)
      }
      case Nil => stack.isEmpty
    }

    loop(str.toList, Nil)
  }
}
