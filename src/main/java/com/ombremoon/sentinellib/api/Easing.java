package com.ombremoon.sentinellib.api;

public abstract class Easing {

    public abstract float easing(float length, float progress);

    public float easing(float progress) { return this.easing(1, progress); }

    public static Easing SINE_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return (float) (length * (1 - Math.cos((progress * Math.PI) / 2)));
        }
    };

    public static Easing SINE_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return (float) (length * Math.sin((progress * Math.PI) / 2));
        }
    };

    public static Easing SINE_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return (float) (length * (-Math.cos(progress * Math.PI) + 1) / 2);
        }
    };

    public static Easing QUAD_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return length * progress * progress;
        }
    };

    public static Easing QUAD_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return length * (1 - (1 - progress) * (1 - progress));
        }
    };

    public static Easing QUAD_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = progress < 0.5F ? 2 * progress * progress : (float) (1 - Math.pow(-2 * progress + 2, 2) / 2);
            return length * f0;
        }
    };

    public static Easing CUBIC_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return length * progress * progress * progress;
        }
    };

    public static Easing CUBIC_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return (float) (length * (1 - Math.pow(1 - progress, 3)));
        }
    };

    public static Easing CUBIC_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = progress < 0.5F ? 4 * progress * progress * progress : (float) (1 - Math.pow(-2 * progress + 2, 3) / 2);
            return length * f0;
        }
    };

    public static Easing QUART_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return length * progress * progress * progress * progress;
        }
    };

    public static Easing QUART_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return (float) (length * (1 - Math.pow(1 - progress, 4)));
        }
    };

    public static Easing QUART_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = progress < 0.5F ? 8 * progress * progress * progress * progress : (float) (1 - Math.pow(-2 * progress + 2, 4) / 2);
            return length * f0;
        }
    };

    public static Easing QUINT_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return length * progress * progress * progress * progress * progress;
        }
    };

    public static Easing QUINT_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return (float) (length * (1 - Math.pow(1 - progress, 5)));
        }
    };

    public static Easing QUINT_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = progress < 0.5F ? 16 * progress * progress * progress * progress * progress : (float) (1 - Math.pow(-2 * progress + 2, 5) / 2);
            return length * f0;
        }
    };

    public static Easing EXP_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = progress == 0 ? 0 : (float) (Math.pow(2, 10 * progress - 10));
            return length * f0;
        }
    };

    public static Easing EXP_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = (float) (1 - Math.pow(2, -10 * progress));
            return length * f0;
        }
    };

    public static Easing EXP_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = progress == 0 ? 0 : progress < 0.5F ? (float) (Math.pow(2, 20 * progress - 10)) : (float) ((2 - Math.pow(2, -20 * progress + 10)) / 2);
            return length * f0;
        }
    };

    public static Easing CIRCLE_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return (float) (length * (1 - Math.sqrt(1 - Math.pow(progress, 2))));
        }
    };

    public static Easing CIRCLE_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return (float) (length * Math.sqrt(1 - Math.pow(progress - 1, 2)));
        }
    };

    public static Easing CIRCLE_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = (float) (progress < 0.5F ? (1 - Math.sqrt(1 - Math.pow(2 * progress, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * progress + 2, 2)) + 1) / 2);
            return length * f0;
        }
    };

    public static Easing BACK_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = 1.70158F;
            float f1 = f0 + 1;
            return length * (f1 * progress * progress * progress - f0 * progress * progress);
        }
    };

    public static Easing BACK_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = 1.70158F;
            float f1 = f0 + 1;
            return (float) (length * (1 + f1 * Math.pow(progress - 1, 3) + f0 * Math.pow(progress - 1, 2)));
        }
    };

    public static Easing BACK_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = 1.70158F;
            float f1 = f0 * 1.525F;
            float f2 = (float) (progress < 0.5F ? (Math.pow(2 * progress, 2) * ((f1 + 1) * 2 * progress - f1)) / 2 : (Math.pow(2 * progress - 2, 2) * ((f1 + 1) * (progress * 2 - 2) + f1) + 2) / 2);
            return length * f2;
        }
    };

    public static Easing ELASTIC_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = (float) ((2 * Math.PI) / 3);
            return progress == 0 ? 0 : (float) (length * (-Math.pow(2, 10 * progress - 10) * Math.sin((progress * 10 - 10.75) * f0)));
        }
    };

    public static Easing ELASTIC_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = (float) ((2 * Math.PI) / 3);
            return progress == 0 ? 0 : (float) (length * (Math.pow(2, -10 * progress) * Math.sin((progress * 10 - 0.75F) * f0) + 1));
        }
    };

    public static Easing ELASTIC_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = (float) ((2 * Math.PI) / 4.5F);
            float f1 = progress == 0 ? 0 : progress < 0.5F ? (float) -(Math.pow(2, 20 * progress - 10) * Math.sin((20 * progress - 11.125) * f0)) / 2 : (float) (Math.pow(2, -20 * progress + 10) * Math.sin((20 * progress - 11.125) * f0)) / 2 + 1;
            return length * f1;
        }
    };

    public static Easing BOUNCE_IN = new Easing() {

        @Override
        public float easing(float length, float progress) {
            return length * (1 - BOUNCE_OUT.easing(1, 1 - progress));
        }
    };

    public static Easing BOUNCE_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = 7.5625F;
            float f1 = 2.75F;
            float f2;
            
            if (progress < 1 / f1) {
                f2 = f0 * progress * progress;
            } else if (progress < 2 / f1) {
                f2 = f0 * (progress -= 1.5F / f1) * progress + 0.75F;
            } else if (progress < 2.5F / f1) {
                f2 = f0 * (progress -= 2.25F / f1) * progress + 0.9375F;
            } else {
                f2 = f0 * (progress -= 2.65F / f1) * progress + 0.984375F;
            }
            return length * f2;
        }
    };

    public static Easing BOUNCE_IN_OUT = new Easing() {

        @Override
        public float easing(float length, float progress) {
            float f0 = progress < 0.5F ? (1 - BOUNCE_OUT.easing(1, 1 - 2 * progress)) / 2 : (1 + BOUNCE_OUT.easing(1, 2 * progress - 1)) / 2;
            return length * f0;
        }
    };
}
