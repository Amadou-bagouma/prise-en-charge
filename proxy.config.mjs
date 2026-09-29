const backendHost = '127.0.0.1';
const backendPort = 8080;

/**
 * @type {import('vite').CommonServerOptions['proxy']}
 */
export default {
  /**
   * Le canal des avis, relayé en tant que connexion persistante.
   *
   * Il lui faut son entrée : sans `ws: true`, le serveur de développement répond lui-même à la
   * demande de bascule et ne la transmet jamais au serveur applicatif. Le navigateur croit la
   * connexion ouverte, le serveur émet dans le vide, et rien n'indique l'un ni l'autre.
   */
  '^/api/avis': {
    target: `http://${backendHost}:${backendPort}`,
    ws: true,
    xfwd: true,
  },
  '^/(api|management|v3/api-docs)': {
    target: `http://${backendHost}:${backendPort}`,
    xfwd: true,
  },
};
