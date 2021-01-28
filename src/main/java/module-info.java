
open module com.sergeysav.hexasphere {
    exports com.sergeysav.hexasphere;

    requires kotlin.stdlib.jdk8;

    requires org.lwjgl;
    requires org.lwjgl.stb;
    requires org.lwjgl.glfw;
    requires org.lwjgl.bgfx;
    requires org.lwjgl.assimp;

    requires org.joml;

    requires kotlin.logging.jvm;
    requires artemis.odb;
}
