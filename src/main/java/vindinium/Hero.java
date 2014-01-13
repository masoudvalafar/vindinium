package vindinium;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * Hero/game player.
 */
public final class Hero {
    // --- Properties ---

    /**
     * Unique ID
     */
    public final int id;

    /**
     * Hero name
     */
    public final String name;

    /**
     * Position/x,y coordinates
     */
    public ImmutablePair<Integer,Integer> position;

    /**
     * Life count
     */
    public int life;

    /**
     * Gold amount
     */
    public int gold;

    /**
     * Is crashed?
     */
    public boolean crashed;

    // --- Constructors ---

    /**
     * Initialize hero.
     */
    public Hero(final int id, final String name,
                final ImmutablePair<Integer,Integer> position,
                final int life, final int gold, final boolean crashed) {

        this.id = id;
        this.name = name;
        this.position = position;
        this.life = life;
        this.gold = gold;
        this.crashed = crashed;
    } // end of <init>

    // --- Object support ---

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Hero)) {
            return false;
        } // end of if

        final Hero other = (Hero) o;

        return new EqualsBuilder().
            append(this.id, other.id).
            append(this.name, other.name).
            append(this.position, other.position).
            append(this.life, other.life).
            append(this.gold, other.gold).
            append(this.crashed, other.crashed).
            isEquals();

    } // end of equals

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return new HashCodeBuilder(1, 5).
            append(this.id).append(this.name).append(this.position).
            append(this.life).append(this.gold).append(this.crashed).
            toHashCode();

    } // end of hashCode

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return new ToStringBuilder(this).
            append("id", this.id).
            append("name", this.name).
            append("position", this.position).
            append("life", this.life).
            append("gold", this.gold).
            append("crashed", this.crashed).
            toString();

    } // end of toString
} // end of class Hero
