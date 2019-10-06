public class g23{


    private static CofinFin s = new CofinFin();

    private static CofinFin t = new CofinFin();

    private static CofinFin u = new CofinFin();

    private static CofinFin v = new CofinFin();

    public static void main(String[] args){ 

        s = (new CofinFin(false, new int[]{8,9,10,11,12,13,14,15}));

        t = (new CofinFin(false, new int[]{4,5,6,7,12,13,14,15}));

        u = (new CofinFin(false, new int[]{2,3,6,7,10,11,14,15}));

        v = (new CofinFin(false, new int[]{1,3,5,7,9,11,13,15}));

        s = s.intersect(t.intersect(u.complement()).complement()).intersect(v.complement());
;


        System.out.println(s.toString());

    }
}
// Successful parse.
