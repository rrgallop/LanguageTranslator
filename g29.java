public class g29{


    private static CofinFin s = new CofinFin();

    private static CofinFin t = new CofinFin();

    private static CofinFin u = new CofinFin();

    private static CofinFin v = new CofinFin();

    private static CofinFin w = new CofinFin();

    public static void main(String[] args){ 

        s = (new CofinFin(false, new int[]{1,2}));

        t = (new CofinFin(false, new int[]{2,3}));

        u = (new CofinFin(true, new int[]{1,2}));

        v = (new CofinFin(true, new int[]{1,3}));

        w = s.intersect(t).union(u.intersect(v));


        System.out.println(w.toString());

    }
}
// Successful parse.
