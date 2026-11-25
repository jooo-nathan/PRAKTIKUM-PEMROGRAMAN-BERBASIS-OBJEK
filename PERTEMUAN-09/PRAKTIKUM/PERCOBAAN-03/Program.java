public class Program {

    public static void main(String[] args) {
        Rektor pakRektor = new Rektor();

        // Sarjana sarjanaCumlaude = new Sarjana("Dini");
        PascaSarjana masterCumlaude = new PascaSarjana("Elok");
        // IBerprestasi prestasi = new IBerprestasi();

        // pakRektor.beriSertifikatCumlaude(sarjanaCumlaude);
        // pakRektor.beriSertifikatCumlaude(masterCumlaude);

        

        // pakRektor.beriSertifikatMawapres(sarjanaCumlaude);
        pakRektor.beriSertifikatMawapres(masterCumlaude);
    }
}


/*
Jawaban Pertanyaan Percobaan 3

1. terjadi error. 

Illegal modifier for the interface method menjuaraiKompetisi; only public, private, abstract, default, static and strictfp are permitted

Illegal combination of modifiers for the private interface method membuatPublikasiIlmiah; additionally only one of static and strictfp is permitted


2. terjadi error karena IBerprestasi abstrak, tidak bisa diinstansiasi


3. 


*/
