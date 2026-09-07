import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'user-management',
    title: 'userManagement.home.title',
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'authority',
    title: 'peccnssApp.adminAuthority.home.title',
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'gestion',
    title: 'peccnssApp.gestion.home.title',
    loadChildren: () => import('./gestion/gestion.routes'),
  },
  {
    path: 'region',
    title: 'peccnssApp.region.home.title',
    loadChildren: () => import('./region/region.routes'),
  },
  {
    path: 'direction',
    title: 'peccnssApp.direction.home.title',
    loadChildren: () => import('./direction/direction.routes'),
  },
  {
    path: 'agent',
    title: 'peccnssApp.agent.home.title',
    loadChildren: () => import('./agent/agent.routes'),
  },
  {
    path: 'ayant-droit',
    title: 'peccnssApp.ayantDroit.home.title',
    loadChildren: () => import('./ayant-droit/ayant-droit.routes'),
  },
  {
    path: 'type-soin',
    title: 'peccnssApp.typeSoin.home.title',
    loadChildren: () => import('./type-soin/type-soin.routes'),
  },
  {
    path: 'etablissement-sante',
    title: 'peccnssApp.etablissementSante.home.title',
    loadChildren: () => import('./etablissement-sante/etablissement-sante.routes'),
  },
  {
    path: 'demande-prise-en-charge',
    title: 'peccnssApp.demandePriseEnCharge.home.title',
    loadChildren: () => import('./demande-prise-en-charge/demande-prise-en-charge.routes'),
  },
  {
    path: 'piece-justificative',
    title: 'peccnssApp.pieceJustificative.home.title',
    loadChildren: () => import('./piece-justificative/piece-justificative.routes'),
  },
  // jhipster-needle-add-entity-route - JHipster will add entity modules routes here
];

export default routes;
