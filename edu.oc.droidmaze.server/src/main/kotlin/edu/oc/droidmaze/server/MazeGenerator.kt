package edu.oc.droidmaze.server

import edu.oc.droidmaze.server.config.Config
import edu.oc.droidmaze.server.config.MazeConfig

object MazeGenerator {
    object Const {
        const val N = 1
        const val S = 2
        const val E = 4
        const val W = 8
        const val U = 16
        const val D = 32
        val list = intArrayOf(N, S, E, W, U, D)
        val dx = mapOf(
            N to 0,
            S to 0,
            E to 1,
            W to -1,
            U to 0,
            D to 0
        )
        val dy = mapOf(
            N to -1,
            S to 1,
            E to 0,
            W to 0,
            U to 0,
            D to 0
        )
        val dz = mapOf(
            N to 0,
            S to 0,
            E to 0,
            W to 0,
            U to -1,
            D to 1
        )
        val opposite = mapOf(
            N to S,
            S to N,
            E to W,
            W to E,
            U to D,
            D to U
        )
    }

    data class Cell(val x: Int, val y: Int, val z: Int)

    fun generateMaze(config: Config): Maze {
        return Maze(generateMaze(config.maze))
    }

    private fun generateMaze(conf: MazeConfig): List<List<List<Int>>> {
        val width = conf.width
        val height = conf.height
        val depth = conf.levels
        val grid = createGrid(width, height, depth)

        val cells = mutableListOf(Cell(x = rand(width), y = rand(height), z = rand(depth)))

        while (cells.isNotEmpty()) {
            val index = if (rand(2) == 0) rand(cells.size) else cells.lastIndex
            val cell = cells[index]
            var carved = false
            val randDirections = randomDirections()

            for (i in randDirections.indices) {
                val dir = randDirections[i]
                val nx = cell.x + Const.dx[dir]!!
                val ny = cell.y + Const.dy[dir]!!
                val nz = cell.z + Const.dz[dir]!!

                if (nx >= 0 && nz >= 0 && ny >= 0 && nx < width && ny < height && nz < depth && grid[nz][ny][nx] == 0) {
                    grid[cell.z][cell.y][cell.x] = grid[cell.z][cell.y][cell.x] or dir
                    grid[nz][ny][nx] = grid[nz][ny][nx] or Const.opposite[dir]!!
                    cells.add(Cell(x = nx, y = ny, z = nz))
                    carved = true
                    break
                }
            }

            if (!carved) {
                cells.removeAt(index)
            }
        }

        return grid
    }

    private fun createGrid(width: Int, height: Int, depth: Int): ArrayList<ArrayList<ArrayList<Int>>> {
        val results = ArrayList<ArrayList<ArrayList<Int>>>()

        for (z in 1..depth) {
            val results2 = ArrayList<ArrayList<Int>>()

            for (y in 1..height) {
                val results3 = ArrayList<Int>()

                for (x in 1..width) {
                    results3.add(0)
                }

                results2.add(results3)
            }

            results.add(results2)
        }

        return results
    }

    private fun rand(n: Int) = Math.floor(Math.random() * n).toInt()

    private fun randomDirections(): IntArray {
        val list = Const.list.copyOf()
        var i = list.lastIndex
        while (i > 0) {
            val j = rand(i + 1)

            val tmp = list[j]
            list[j] = list[i]
            list[i] = tmp

            i--
        }
        return list
    }
}
