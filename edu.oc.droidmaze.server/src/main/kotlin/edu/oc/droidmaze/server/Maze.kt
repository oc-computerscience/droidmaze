package edu.oc.droidmaze.server

import com.google.common.base.MoreObjects
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File

class Maze(inList: List<List<List<Int>>>) {

    @JvmField
    var slots: List<List<List<Slot>>>

    val width
        get() = slots.first().first().size

    val height
        get() = slots.first().size

    val depth
        get() = slots.size

    init {
        val levels = inList.size
        val height = inList.first().size
        val width = inList.first().first().size

        val tempSlots = ArrayList<ArrayList<ArrayList<Slot>>>(levels * 2 + 1)

        // first layer is all walls
        val firstLayer = ArrayList<ArrayList<Slot>>(height * 2 + 1)
        tempSlots += firstLayer
        for (i in 0 until height * 2 + 1) {
            val row = ArrayList<Slot>(width * 2 + 1)
            firstLayer += row
            for (j in 0 until width * 2 + 1) {
                row += Slot()
            }
        }

        for (i in 0 until levels) {
            val level = ArrayList<ArrayList<Slot>>(height * 2 + 1)
            val nextLevel = ArrayList<ArrayList<Slot>>(height * 2 + 1)
            tempSlots += level
            tempSlots += nextLevel

            // Add all walls on the first row
            // This is the "top" (northernmost) edge, which is always walls
            val firstRow = ArrayList<Slot>(width * 2 + 1)
            val nextLevelFirstRow = ArrayList<Slot>(width * 2 + 1)
            level += firstRow
            nextLevel += nextLevelFirstRow
            for (k in 0 until width * 2 + 1) {
                firstRow += Slot()
                nextLevelFirstRow += Slot()
            }

            for (j in 0 until height) {
                val row = ArrayList<Slot>(width * 2 + 1)
                val nextRow = ArrayList<Slot>(width * 2 + 1)
                level += row
                level += nextRow
                // Next level needs these too
                val nextLevelRow = ArrayList<Slot>(width * 2 + 1)
                val nextLevelNextRow = ArrayList<Slot>(width * 2 + 1)
                nextLevel += nextLevelRow
                nextLevel += nextLevelNextRow

                // Left-most edge is always a wall
                row += Slot()
                nextRow += Slot()
                nextLevelRow += Slot()
                nextLevelNextRow += Slot()

                for (k in 0 until width) {
                    val value = inList[i][j][k]
                    val slot = Slot()
                    row += slot

                    // North
                    if (value and MazeGenerator.Const.N == MazeGenerator.Const.N) {
                        addNorth(slot, tempSlots)
                    }
                    // West
                    if (value and MazeGenerator.Const.W == MazeGenerator.Const.W) {
                        addWest(slot, tempSlots)
                    }
                    // Up
                    if (value and MazeGenerator.Const.U == MazeGenerator.Const.U) {
                        addUp(slot, tempSlots)
                    }
                    // South
                    if (value and MazeGenerator.Const.S == MazeGenerator.Const.S) {
                        addSouth(slot, tempSlots)
                    } else {
                        // Add wall to the south if we don't connect to the south
                        tempSlots.south() += Slot()
                    }
                    tempSlots.downSouth() += Slot()
                    // Down
                    if (value and MazeGenerator.Const.D == MazeGenerator.Const.D) {
                        addDown(slot, tempSlots)
                    } else {
                        tempSlots.down() += Slot()
                    }
                    // East
                    if (value and MazeGenerator.Const.E == MazeGenerator.Const.E) {
                        addEast(slot, tempSlots)
                    } else {
                        // Add wall to the east if we don't connect to the east
                        tempSlots.east() += Slot()
                    }
                    tempSlots.downEast() += Slot()

                    // In this block of 8 we're creating on each iteration, the upper-south-eastern slot is always a wall
                    tempSlots.secondToLast().last() += Slot()
                    // And in this bloc kfo 8 we're creating on each iteration, the lower-south-eastern lost is also a wall
                    tempSlots.last().last() += Slot()
                }
            }
        }

        slots = tempSlots
    }

    fun write(file: File) {
        file.delete()
        DataOutputStream(BZip2CompressorOutputStream(file.outputStream().buffered())).use {
            it.writeShort((slots.size - 1) / 2)
            it.writeShort((slots.first().size - 1) / 2)
            it.writeShort((slots.first().first().size - 1) / 2)

            // Only a few of the numbers are needed to describe the maze
            // the rest of the slots are used for walls
            for (i in 1 until slots.size step 2) {
                val rows = slots[i]
                for (j in 1 until rows.size step 2) {
                    val row = rows[j]
                    for (k in 1 until row.size step 2) {
                        it.writeByte(row[k].num)
                    }
                }
            }

            it.flush()
        }
    }

    companion object {
        fun read(file: File): Maze? {
            if (!file.exists()) {
                LOGGER.warn("${file.absoluteFile} does not exist")
                return null
            }

            DataInputStream(BZip2CompressorInputStream(file.inputStream().buffered())).use {
                val depth = it.readShort()
                val height = it.readShort()
                val width = it.readShort()

                val list = ArrayList<ArrayList<ArrayList<Int>>>()
                for (i in 0 until depth) {
                    val layer = ArrayList<ArrayList<Int>>()
                    list.add(layer)
                    for (j in 0 until height) {
                        val column = ArrayList<Int>()
                        layer.add(column)
                        for (k in 0 until width) {
                            column.add(it.readByte().toInt())
                        }
                    }
                }

                return Maze(list)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Maze) {
            return false
        }

        if (other === this) {
            return true
        }

        if (this.depth != other.depth || this.width != other.width || this.height != other.height) {
            return false
        }

        for (i in slots.indices) {
            val level = slots[i]
            for (j in level.indices) {
                val row = level[j]
                for (k in row.indices) {
                    if (row[k].num != other.slots[i][j][k].num) {
                        return false
                    }
                }
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = 1

        result = 31 * result + depth
        result = 31 * result + width
        result = 31 * result + height

        slots.asSequence()
            .flatMap { it.asSequence() }
            .flatMap { it.asSequence() }
            .forEach { result = 31 * result + it.num }

        return result
    }
}

private fun addNorth(slot: Slot, slots: ArrayList<ArrayList<ArrayList<Slot>>>) {
    val otherSlot = slots.north()
    slot.north = otherSlot
    otherSlot.south = slot
}

private fun addWest(slot: Slot, slots: ArrayList<ArrayList<ArrayList<Slot>>>) {
    val otherSlot = slots.west()
    slot.west = otherSlot
    otherSlot.east = slot
}

private fun addUp(slot: Slot, slots: ArrayList<ArrayList<ArrayList<Slot>>>) {
    val otherSlot = slots.up()
    slot.up = otherSlot
    otherSlot.down = slot
}

private fun addSouth(slot: Slot, slots: ArrayList<ArrayList<ArrayList<Slot>>>) {
    val southSlot = Slot()
    slots.south() += southSlot
    slot.south = southSlot
    southSlot.north = slot
}

private fun addEast(slot: Slot, slots: ArrayList<ArrayList<ArrayList<Slot>>>) {
    val eastSlot = Slot()
    slots.east() += eastSlot
    slot.east = eastSlot
    eastSlot.west = slot
}

private fun addDown(slot: Slot, slots: ArrayList<ArrayList<ArrayList<Slot>>>) {
    val downSlot = Slot()
    slots.down() += downSlot
    slot.down = downSlot
    downSlot.up = slot
}

data class Slot(
    var north: Slot? = null,
    var south: Slot? = null,
    var east: Slot? = null,
    var west: Slot? = null,
    var up: Slot? = null,
    var down: Slot? = null
) {
    val isWall
        get() = north == null && south == null && east == null && west == null && up == null && down == null

    val num: Int
        get() {
            val n = if (north == null) 0 else MazeGenerator.Const.N
            val s = if (south == null) 0 else MazeGenerator.Const.S
            val e = if (east == null) 0 else MazeGenerator.Const.E
            val w = if (west == null) 0 else MazeGenerator.Const.W
            val u = if (up == null) 0 else MazeGenerator.Const.U
            val d = if (down == null) 0 else MazeGenerator.Const.D
            return n or s or e or w or u or d
        }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("north", north != null)
            .add("south", south != null)
            .add("east", east != null)
            .add("west", west != null)
            .add("up", up != null)
            .add("down", down != null)
            .toString()
    }
}

private fun <T, L : ArrayList<T>> L.secondToLast() = this[lastIndex - 1]
private fun <T, L : ArrayList<T>> L.thirdToLast() = this[lastIndex - 2]

private fun <T> ArrayList<ArrayList<ArrayList<T>>>.north() = secondToLast().thirdToLast()[secondToLast().secondToLast().lastIndex]
private fun <T> ArrayList<ArrayList<ArrayList<T>>>.up() = thirdToLast()[secondToLast().lastIndex - 1][secondToLast().secondToLast().lastIndex]
private fun <T> ArrayList<ArrayList<ArrayList<T>>>.west() = secondToLast().secondToLast().secondToLast()
private fun <T> ArrayList<ArrayList<ArrayList<T>>>.south() = secondToLast().last()
private fun <T> ArrayList<ArrayList<ArrayList<T>>>.east() = secondToLast().secondToLast()
private fun <T> ArrayList<ArrayList<ArrayList<T>>>.down() = last().secondToLast()

private fun <T> ArrayList<ArrayList<ArrayList<T>>>.downSouth() = last().last()
private fun <T> ArrayList<ArrayList<ArrayList<T>>>.downEast() = last().secondToLast()


