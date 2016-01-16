package ledkis.module.picturecomparator.util;

public class Geometry3D {

    public static final Point3D CENTER_POINT_3D = new Point3D(0f, 0f, 0f);

    public static class Point3D {
        public final float x, y, z;

        public Point3D(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point3D translateY(float distance) {
            return new Point3D(x, y + distance, z);
        }

        public Point3D translate(Vector3D vector3D) {
            return new Point3D(
                    x + vector3D.x,
                    y + vector3D.y,
                    z + vector3D.z);
        }
    }

    public static class Vector3D {
        public final float x, y, z;

        public Vector3D(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float) Math.sqrt(
                    x * x
                            + y * y
                            + z * z);
        }

        // http://en.wikipedia.org/wiki/Cross_product        
        public Vector3D crossProduct(Vector3D other) {
            return new Vector3D(
                    (y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x));
        }

        // http://en.wikipedia.org/wiki/Dot_product
        public float dotProduct(Vector3D other) {
            return x * other.x
                    + y * other.y
                    + z * other.z;
        }

        public Vector3D scale(float f) {
            return new Vector3D(
                    x * f,
                    y * f,
                    z * f);
        }
    }

    public static class Ray {
        public final Point3D point3D;
        public final Vector3D vector3D;

        public Ray(Point3D point3D, Vector3D vector3D) {
            this.point3D = point3D;
            this.vector3D = vector3D;
        }
    }

    // TODO: Re-use shared stuff in classes as an exercise

    public static class Circle3D {
        public final Point3D center;
        public final float radius;

        public Circle3D(Point3D center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle3D scale(float scale) {
            return new Circle3D(center, radius * scale);
        }
    }

    public static class Cylinder {
        public final Point3D center;
        public final float radius;
        public final float height;

        public Cylinder(Point3D center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

    public static class Sphere {
        public final Point3D center;
        public final float radius;

        public Sphere(Point3D center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static class Rect3D {
        // TODO : change Point3D by Plane3D
        public final Point3D center;
        public final float width;
        public final float height;

        public Rect3D(Point3D center, float width, float height) {
            this.center = center;
            this.width = width;
            this.height = height;
        }
    }

    public static class Plane3D {
        public final Point3D point3D;
        public final Vector3D normal;

        public Plane3D(Point3D point3D, Vector3D normal) {
            this.point3D = point3D;
            this.normal = normal;
        }
    }

    public static Vector3D vectorBetween3D(Point3D from, Point3D to) {
        return new Vector3D(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween3D(sphere.center, ray) < sphere.radius;
    }

    public static boolean intersects(Rect3D rect3D, Ray ray) {
        // TODO simplification : rect.width
        return distanceBetween3D(rect3D.center, ray) < rect3D.width;
    }

    // http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
    // Note that this formula treats Ray as if it extended infinitely past
    // either point.
    public static float distanceBetween3D(Point3D point3D, Ray ray) {
        Vector3D p1ToPoint = vectorBetween3D(ray.point3D, point3D);
        Vector3D p2ToPoint = vectorBetween3D(ray.point3D.translate(ray.vector3D), point3D);

        // The length of the cross product gives the area of an imaginary
        // parallelogram having the two vectors as sides. A parallelogram can be
        // thought of as consisting of two triangles, so this is the same as
        // twice the area of the triangle defined by the two vectors.
        // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector3D.length();

        // The area of a triangle is also equal to (base * height) / 2. In
        // other words, the height is equal to (area * 2) / base. The height
        // of this triangle is the distance from the point to the ray.
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    // http://en.wikipedia.org/wiki/Line-plane_intersection
    // This also treats rays as if they were infinite. It will return a
    // point full of NaNs if there is no intersection point.
    public static Point3D intersectionPoint(Ray ray, Plane3D plane3D) {
        Vector3D rayToPlaneVector3D = vectorBetween3D(ray.point3D, plane3D.point3D);

        float scaleFactor = rayToPlaneVector3D.dotProduct(plane3D.normal)
                / ray.vector3D.dotProduct(plane3D.normal);

        Point3D intersectionPoint3D = ray.point3D.translate(ray.vector3D.scale(scaleFactor));
        return intersectionPoint3D;
    }
}
