package se.fusion1013.cobaltKingdoms.entities.armorstand;

import io.papermc.paper.math.Rotations;
import org.bukkit.entity.ArmorStand;
import se.fusion1013.cobaltCore.util.INameProvider;

import java.util.Objects;

public final class ArmorStandPose implements INameProvider {
    private final String internalName;
    private Rotations body;
    private Rotations head;
    private Rotations leftArm;
    private Rotations leftLeg;
    private Rotations rightArm;
    private Rotations rightLeg;

    public ArmorStandPose(String internalName, Rotations body, Rotations head, Rotations leftArm, Rotations leftLeg, Rotations rightArm,
                          Rotations rightLeg) {
        this.internalName = internalName;
        this.body = body;
        this.head = head;
        this.leftArm = leftArm;
        this.leftLeg = leftLeg;
        this.rightArm = rightArm;
        this.rightLeg = rightLeg;
    }

    public ArmorStandPose(String internalName) {
        this(internalName, Rotations.ZERO, Rotations.ZERO, Rotations.ZERO, Rotations.ZERO, Rotations.ZERO, Rotations.ZERO);
    }

    public void apply(ArmorStand armorStand) {
        armorStand.setBodyRotations(body);
        armorStand.setHeadRotations(head);
        armorStand.setLeftArmRotations(leftArm);
        armorStand.setLeftLegRotations(leftLeg);
        armorStand.setRightArmRotations(rightArm);
        armorStand.setRightLegRotations(rightLeg);
    }

    public Rotations body() {
        return body;
    }

    public Rotations head() {
        return head;
    }

    public Rotations leftArm() {
        return leftArm;
    }

    public Rotations leftLeg() {
        return leftLeg;
    }

    public Rotations rightArm() {
        return rightArm;
    }

    public Rotations rightLeg() {
        return rightLeg;
    }

    public String getInternalName() {
        return internalName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ArmorStandPose) obj;
        return Objects.equals(this.body, that.body) &&
                Objects.equals(this.head, that.head) &&
                Objects.equals(this.leftArm, that.leftArm) &&
                Objects.equals(this.leftLeg, that.leftLeg) &&
                Objects.equals(this.rightArm, that.rightArm) &&
                Objects.equals(this.rightLeg, that.rightLeg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, head, leftArm, leftLeg, rightArm, rightLeg);
    }

    @Override
    public String toString() {
        return "ArmorStandPose[" +
                "body=" + body + ", " +
                "head=" + head + ", " +
                "leftArm=" + leftArm + ", " +
                "leftLeg=" + leftLeg + ", " +
                "rightArm=" + rightArm + ", " +
                "rightLeg=" + rightLeg + ']';
    }


    public static class Builder {

        public final String internalName;
        private final ArmorStandPose obj;

        public Builder(String internalName) {
            this.internalName = internalName;
            obj = new ArmorStandPose(internalName);
        }

        public ArmorStandPose build() {
            return obj;
        }

        public Builder setBodyRotation(Rotations body) {
            obj.body = body;
            return this;
        }

        public Builder setHeadRotation(Rotations head) {
            obj.head = head;
            return this;
        }

        public Builder setLeftArmRotation(Rotations leftArm) {
            obj.leftArm = leftArm;
            return this;
        }

        public Builder setLeftLegRotation(Rotations leftLeg) {
            obj.leftLeg = leftLeg;
            return this;
        }

        public Builder setRightArmRotation(Rotations rightArm) {
            obj.rightArm = rightArm;
            return this;
        }

        public Builder setRightLegRotation(Rotations rightLeg) {
            obj.rightLeg = rightLeg;
            return this;
        }

    }

}
