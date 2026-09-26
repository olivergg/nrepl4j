(ns io.olivergg.nrepl4j.cdi
  "CDI bean lookup for the REPL. Load only in apps running a CDI container
   (Weld, OpenWebBeans...). Uses jakarta.enterprise.* - swap for
   javax.enterprise.* on pre-Jakarta-EE-9 containers.")

(defn get-bean
  "A CDI-managed bean of the given type, resolving any ambiguity with qualifiers."
  [type & qualifiers]
  (-> (jakarta.enterprise.inject.spi.CDI/current)
      (.select type (into-array java.lang.annotation.Annotation qualifiers))
      .get))
