package com.compomics.util.experiment.identification.protein_inference.fm_index;

/**
 * Rank as used in the FM index.
 *
 * @author Dominik Kopczynski
 */
public class Rank {

    /**
     * The length.
     */
    public int length;
    /**
     * The bit field.
     */
    private long[] bitfield;
    /**
     * The sums.
     */
    private int[] sums;
    /**
     * Binary: 0101.
     */
    private final long m1 = 0x5555555555555555L;
    /**
     * Binary: 00110011.
     */
    private final long m2 = 0x3333333333333333L;
    /**
     * Binary: 4 zeros, 4 ones.
     */
    private final long m4 = 0x0f0f0f0f0f0f0f0fL;
    /**
     * Binary: 8 zeros, 8 ones.
     */
    private final long m8 = 0x00ff00ff00ff00ffL;
    /**
     * Binary: 16 zeros, 16 ones.
     */
    private final long m16 = 0x0000ffff0000ffffL;
    /**
     * Binary: 32 zeros, 32 ones.
     */
    private final long m32 = 0x00000000ffffffffL;
    /**
     * Binary: all ones.
     */
    private final long hff = 0xffffffffffffffffL;
    /**
     * The sum of 256 to the power of 0,1,2,3.
     */
    private final long h01 = 0x0101010101010101L;
    /**
     * The shift.
     */
    private final int shift = 6;
    /**
     * The mask.
     */
    private final int mask = 63;

    /**
     * Constructor.
     *
     * @param text the text
     * @param aAlphabet the alphabet
     */
    public Rank(byte[] text, long[] aAlphabet) {
        long[] alphabet = new long[2];
        alphabet[0] = aAlphabet[0];
        alphabet[1] = aAlphabet[1];
        length = text.length;
        int half = ((popcount(alphabet[0]) + popcount(alphabet[1])) - 1) >> 1;
        //long half = (len_alphabet - 1) >> 1;
        int cnt = 0;

        for (int i = 0; i < 128 && cnt <= half; ++i) {
            int cell = i >> 6;
            int pos = i & 63;
            cnt += (alphabet[cell] >> pos) & 1L;
            alphabet[cell] &= ~(1L << pos);
        }

        int field_len = (length >> 6) + 1;
        bitfield = new long[(int) field_len];
        sums = new int[(int) field_len];
        sums[0] = 0;

        for (int i = 0; i < length; ++i) {
            int cell = i >> 6;
            int pos = i & 63;
            if (pos == 0) {
                bitfield[cell] = 0;
            }
            long bit = (alphabet[(int) (text[(int) i] >> 6L)] >> (text[(int) i] & 63L)) & 1L;
            bitfield[cell] |= (bit << pos);

            if (pos == 0 && i != 0) {
                sums[cell] = sums[cell - 1] + popcount(bitfield[cell - 1]);
            }
        }
    }

    /**
     * Pop count.
     *
     * @param x the value
     * @return the result
     */
    public int popcount(long x) {
        x -= (x >> 1) & m1;             // put count of each 2 bits into those 2 bits
        x = (x & m2) + ((x >> 2) & m2);    // put count of each 4 bits into those 4 bits 
        x = (x + (x >> 4)) & m4;         // put count of each 8 bits into those 8 bits 
        return (int) ((x * h01) >> 56);     // returns left 8 bits of x + (x<<8) + (x<<16) + (x<<24) + ... 
    }

    /**
     * Returns the rank.
     *
     * @param i the value
     * @param counter the counter
     * @return the rank
     */
    public int getRank(int i, boolean counter) {
        if (0 <= i && i < length) {
            int cell = i >> shift;
            int pos = i & mask;
            int masked = mask - pos;
            long active_ones = bitfield[cell] << masked;
            int count_ones = sums[cell];
            i += 1;
            count_ones += popcount(active_ones);
            return counter ? i - count_ones : count_ones;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    /**
     * Returns true if the value is equal to one.
     *
     * @param i the value
     * @return true if the value is equal to one
     */
    public boolean isOne(int i) {
        if (0 <= i && i < length) {
            int cell = i >> shift;
            int pos = i & mask;
            if (((bitfield[cell] >> pos) & 1L) == 1) {
                return true;
            }
            return false;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
}
