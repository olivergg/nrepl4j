(ns io.olivergg.nrepl4j.spring
  "Spring bean lookup for the REPL. Load only in apps that have Spring on the
   classpath - this file is not compiled with the library, only when read into
   a running app via NReplServer.loadClasspathResource. Requires the app to have
   published its ApplicationContext via NReplContext.put(\"applicationContext\", ctx)
   before this file loads (embedded Spring Boot has no static context holder).")

(defn application-context
  "The ApplicationContext the host app published into NReplContext."
  ^org.springframework.context.ApplicationContext []
  (or (io.olivergg.nrepl4j.NReplContext/get "applicationContext")
      (throw (IllegalStateException.
               "No ApplicationContext in NReplContext - call NReplContext.put(\"applicationContext\", ctx) at startup"))))

(defn get-bean
  "A Spring bean by id (a String) or by type (a Class)."
  [id-or-type]
  (.getBean (application-context) id-or-type))
