import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

// el Boostrap s'utilitza per inicialitzar la bases de dades segons el continut del fitxer
// initial-data.yml, si la base de dades està buida (no conté cap usuari).
@OnApplicationStart
public class Bootstrap extends Job {

    public void doJob(){
        // Mira si les bases de dades estàn buides
        if(User.count() == 0)
        {
            Fixtures.loadModels("initial-data.yml");
        }
    }
}
