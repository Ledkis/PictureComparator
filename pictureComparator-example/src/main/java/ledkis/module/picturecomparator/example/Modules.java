package ledkis.module.picturecomparator.example;

final class Modules {
    static Object[] list(PictureComparatorApplication app) {
        return new Object[]{
                new PictureComparatorModule(app)
        };
    }

    private Modules() {
        // No instances.
    }
}
