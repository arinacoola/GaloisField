public class ElementFields {
    public static final int m = 239;
    public static final int el=4;
    private final long[] valEl;

    public ElementFields(){
        this.valEl=new long[el];
    }

    public ElementFields(ElementFields other){
        this.valEl=other.valEl.clone();
    }

    public static ElementFields one(){
        ElementFields e = new ElementFields();
        e.setBit(0);
        return e;
    }

    public static ElementFields zero(){
        return new ElementFields();
    }

    public boolean getBit(int i){
        if (i < 0 || i >= m) {
            throw new IndexOutOfBoundsException("bit index out of range");
        }
        int elem = i / 64;
        int bit  = i % 64;
        return ((valEl[elem] >>> bit) & 1L) == 1L;
    }

    public void  setBit(int i){
        if((i < 0) || (i >= m)){
            throw new IndexOutOfBoundsException("bit index out of range");
        }
        int elem=i/64;
        int bit=i%64;
        valEl[elem] = valEl[elem] | (1L << bit);
    }

    public void clearBit(int i){
        if((i < 0) || (i >= m)){
            throw new IndexOutOfBoundsException("bit index out of range");
        }
        int elem=i/64;
        int bit=i%64;
        valEl[elem]= valEl[elem]& ~(1L << bit);
    }

    public int findDegree() {
        for (int elem = el - 1; elem >= 0; elem--) {
            if (valEl[elem] != 0) {
                int posBit = 63 - Long.numberOfLeadingZeros(valEl[elem]);
                return elem * 64 + posBit;
            }
        }
        return -1;
    }

    public void changeBit(int i){
        if((i < 0) || (i >= m)){
            throw new IndexOutOfBoundsException("bit index out of range");
        }
        int elem=i/64;
        int bit=i%64;
        valEl[elem]=valEl[elem]^(1L << bit);
    }

    public String  toBitString(){
        StringBuilder sb = new StringBuilder(m );
        for (int i =m - 1; i >= 0; i--) {
            if (getBit(i)) {
                sb.append('1');
            }
            else {
                sb.append('0');
            }
        }
        return sb.toString();
    }

    public static ElementFields fromBitString(String s){
        if (s.length() != m) {
            throw new IllegalArgumentException("the bit string must have a length of " + m);
        }
        ElementFields e = new ElementFields();
        for (int i = 0; i < ElementFields.m; i++) {
            char c = s.charAt(ElementFields.m - 1 - i);
            if (c == '1') {
                e.setBit(i);
            }
            else if (c != '0') {
                throw new IllegalArgumentException("invalid character: " + c);
            }
        }
        return e;
    }


    public long getElem(int i){
        return valEl[i];
    }
    public void setElem(int i, long v){
        valEl[i] = v;
    }
}
