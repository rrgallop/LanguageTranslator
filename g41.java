public class g41{


    private static CofinFin s = new CofinFin();

    private static CofinFin t = new CofinFin();

    private static CofinFin u = new CofinFin();

    private static CofinFin v = new CofinFin();

    private static CofinFin x = new CofinFin();

    private static CofinFin y = new CofinFin();

    private static CofinFin z = new CofinFin();

    private static CofinFin w = new CofinFin();

    public static void main(String[] args){ 

        s = (new CofinFin(false, new int[]{}));

        t = (new CofinFin(false, new int[]{1,2,3,4,5}));

        u = (new CofinFin(false, new int[]{1,2}));

        v = (new CofinFin(true, new int[]{}));

        x = (new CofinFin(true, new int[]{1,3,5,7}));

        y = (new CofinFin(false, new int[]{2,4,6}));

        z = (new CofinFin(true, new int[]{1,2,3,4,5,6,7,8,9,0}));

        w = s.complement().intersect(t.union(u.intersect(v)).union(y).complement()).intersect(z.intersect(x).complement());


        System.out.println(w.toString());

    }
}
// Successful parse.
