import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Service, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT, serverApiUrl } from 'app/config';
import { createRequestOption } from 'app/core/request';
import { IAgent, NewAgent } from '../agent.model';

export type PartialUpdateAgent = Partial<IAgent> & Pick<IAgent, 'id'>;

type RestOf<T extends IAgent | NewAgent> = Omit<T, 'dateNaissance'> & {
  dateNaissance?: string | null;
};

export type RestAgent = RestOf<IAgent>;

export type NewRestAgent = RestOf<NewAgent>;

export type PartialUpdateRestAgent = RestOf<PartialUpdateAgent>;

@Service()
export class AgentsService {
  readonly agentsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly agentsResource = httpResource<RestAgent[]>(() => {
    const params = this.agentsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of agent that have been fetched. It is updated when the agentsResource emits a new value.
   * In case of error while fetching the agents, the signal is set to an empty array.
   */
  readonly agents = computed(() =>
    (this.agentsResource.hasValue() ? this.agentsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly resourceUrl = `${serverApiUrl}api/agents`;

  protected convertValueFromServer(restAgent: RestAgent): IAgent {
    return {
      ...restAgent,
      dateNaissance: restAgent.dateNaissance ? dayjs(restAgent.dateNaissance) : undefined,
    };
  }
}

@Service()
export class AgentService extends AgentsService {
  protected readonly http = inject(HttpClient);

  create(agent: NewAgent): Observable<IAgent> {
    const copy = this.convertValueFromClient(agent);
    return this.http.post<RestAgent>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(agent: IAgent): Observable<IAgent> {
    const copy = this.convertValueFromClient(agent);
    return this.http
      .put<RestAgent>(`${this.resourceUrl}/${encodeURIComponent(this.getAgentIdentifier(agent))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(agent: PartialUpdateAgent): Observable<IAgent> {
    const copy = this.convertValueFromClient(agent);
    return this.http
      .patch<RestAgent>(`${this.resourceUrl}/${encodeURIComponent(this.getAgentIdentifier(agent))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IAgent> {
    return this.http.get<RestAgent>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IAgent[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAgent[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAgentIdentifier(agent: Pick<IAgent, 'id'>): number {
    return agent.id;
  }

  compareAgent(o1: Pick<IAgent, 'id'> | null, o2: Pick<IAgent, 'id'> | null): boolean {
    return o1 && o2 ? this.getAgentIdentifier(o1) === this.getAgentIdentifier(o2) : o1 === o2;
  }

  addAgentToCollectionIfMissing<Type extends Pick<IAgent, 'id'>>(
    agentCollection: Type[],
    ...agentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const agents: Type[] = agentsToCheck.filter(agentItem => agentItem !== null && agentItem !== undefined);
    if (agents.length > 0) {
      const agentCollectionIdentifiers = agentCollection.map(agentItem => this.getAgentIdentifier(agentItem));
      const agentsToAdd = agents.filter(agentItem => {
        const agentIdentifier = this.getAgentIdentifier(agentItem);
        if (agentCollectionIdentifiers.includes(agentIdentifier)) {
          return false;
        }
        agentCollectionIdentifiers.push(agentIdentifier);
        return true;
      });
      return [...agentsToAdd, ...agentCollection];
    }
    return agentCollection;
  }

  protected convertValueFromClient<T extends IAgent | NewAgent | PartialUpdateAgent>(agent: T): RestOf<T> {
    return {
      ...agent,
      dateNaissance: agent.dateNaissance?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestAgent): IAgent {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestAgent[]): IAgent[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
