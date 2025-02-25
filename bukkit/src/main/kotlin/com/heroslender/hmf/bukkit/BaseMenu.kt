package com.heroslender.hmf.bukkit

import com.heroslender.hmf.bukkit.map.MapCanvas
import com.heroslender.hmf.bukkit.screen.MenuScreen
import com.heroslender.hmf.bukkit.screen.privateMenuScreenOf
import com.heroslender.hmf.bukkit.sdk.Direction
import com.heroslender.hmf.bukkit.utils.BoundingBox
import com.heroslender.hmf.bukkit.utils.boundingBoxOf
import com.heroslender.hmf.bukkit.utils.ignore
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.Action

abstract class BaseMenu(
    val owner: Player,
    val width: Int = 4,
    val height: Int = 3,
    val direction: Direction = Direction.from(owner).opposite(),
    val manager: BukkitMenuManager,
    override val context: BukkitContext = Context(manager, MapCanvas(width * 128, height * 128)),
    val opts: MenuOptions = MenuOptions(),
) : BukkitMenu {
    var startX: Int = 0
    var startY: Int = 0
    var startZ: Int = 0

    private var screen: MenuScreen? = null
    final override var boundingBox: BoundingBox = BoundingBox.EMPTY

    fun hasEntityId(id: Int): Boolean {
        return screen?.chunks?.any { it.id == id } ?: false
    }

    init {
        val startScreen: Location = owner.location.clone()
            .apply { pitch = 0F }
            .let { it.add(it.direction.multiply(2)) }

        val left = direction.rotateLeft()
        val startOffset = -(width / 2 - 1).toDouble()
        startScreen.add(startOffset * left.x, 2.0, startOffset * left.z)

        this.startX = startScreen.blockX
        this.startY = startScreen.blockY
        this.startZ = startScreen.blockZ

        this.boundingBox = calculateBoundingBox()
    }

    fun send() {
        this.screen = manager.withEntityIdFactory { nextEntityId ->
            manager.add(this)

            privateMenuScreenOf(
                owner,
                opts,
                width,
                height,
                startX,
                startY,
                startZ,
                direction,
                nextEntityId
            )
        }

        screen?.spawn()

        context.onUpdate {
            screen?.update(context.canvas)
        }

        render()
    }

    fun destroy() {
        screen?.despawn()

        manager.remove(owner)
    }

    override fun tickCursor() = raytrace { x, y ->
        screen?.updateCursor((x * 128).toInt(), (y * 128).toInt())
    }.ignore()

    override fun onInteract(action: Action): Boolean = raytrace { x, y ->
        val mapX = (x * 128).toInt()
        val mapY = (y * 128).toInt()

        val type = when (action) {
            Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR ->
                ClickEvent.Type.RIGHT_CLICK
            else ->
                ClickEvent.Type.LEFT_CLICK
        }

        context.handleClick(mapX, mapY, type)
    }

    private inline fun raytrace(onIntersect: (x: Double, y: Double) -> Unit): Boolean {
        val intersection = boundingBox.rayTrace(
            start = owner.eyeLocation.toVector(),
            direction = owner.location.direction,
            maxDistance = manager.opts.maxInteractDistance
        ) ?: return false

        val rd = direction.rotateLeft()
        val x = if (rd.x != 0) {
            (intersection.x - boundingBox.minX) * rd.x
        } else {
            (intersection.z - boundingBox.minZ) * rd.z
        }
        val y = boundingBox.maxY - intersection.y

        onIntersect(if (x < 0) x + width else x, y)
        return true
    }

    private fun calculateBoundingBox(): BoundingBox {
        val bbStartX: Double
        val bbEndX: Double
        when (direction.x) {
            -1 -> {
                bbStartX = startX + .9375
                bbEndX = bbStartX
            }
            1 -> {
                bbStartX = startX + .0625
                bbEndX = bbStartX
            }
            0 -> { // Equals to 0
                val left = direction.rotateLeft()
                bbStartX = if (left.x == -1) (startX + 1).toDouble() else startX.toDouble()
                bbEndX = bbStartX + width * left.x
            }
            else -> {
                bbStartX = startX.toDouble()
                bbEndX = bbStartX
            }
        }

        val bbStartZ: Double
        val bbEndZ: Double
        when (direction.z) {
            -1 -> {
                bbStartZ = startZ + .9375
                bbEndZ = bbStartZ
            }
            1 -> {
                bbStartZ = startZ + .0625
                bbEndZ = bbStartZ
            }
            0 -> {
                val left = direction.rotateLeft()
                bbStartZ = if (left.z == -1) (startZ + 1).toDouble() else startZ.toDouble()
                bbEndZ = bbStartZ + width * left.z
            }
            else -> {
                bbStartZ = startZ.toDouble()
                bbEndZ = bbStartZ
            }
        }

        return boundingBoxOf(
            bbStartX,
            startY - (height - 1.0),
            bbStartZ,
            bbEndX,
            startY + 1.0,
            bbEndZ,
        )
    }
}