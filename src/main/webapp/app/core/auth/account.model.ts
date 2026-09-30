export class Account {
  constructor(
    public activated: boolean,
    public authorities: string[],
    public email: string,
    public firstName: string | null,
    public langKey: string,
    public lastName: string | null,
    public login: string,
    public imageUrl: string | null,
    public mustChangePassword?: boolean,
    /** Profil affecté à l'agent : c'est lui qui détermine ses authorities. */
    public profil?: { id: number; nom?: string | null } | null,
    /**
     * L'agent dont ce compte est l'accès personnel, s'il en est un.
     *
     * Absent pour le personnel administratif, qui instruit les dossiers des autres sans être
     * lui-même bénéficiaire. Présent, il ouvre l'espace personnel — et le serveur borne alors
     * ce que les écrans rapportent à ce seul agent.
     */
    public agent?: { id: number; matricule?: string | null; nom?: string | null; prenom?: string | null } | null,
  ) {}
}
