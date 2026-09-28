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
  ) {}
}
