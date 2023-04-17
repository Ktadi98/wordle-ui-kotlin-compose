package clases

class Row(var caselles : Array<Casella>) {
    fun setCasella(index:Int,c: Casella) {
        this.caselles[index] = c
    }
}
