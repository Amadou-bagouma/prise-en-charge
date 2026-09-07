import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAgent, NewAgent } from '../agent.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAgent for edit and NewAgentFormGroupInput for create.
 */
type AgentFormGroupInput = IAgent | PartialWithRequiredKeyOf<NewAgent>;

type AgentFormDefaults = Pick<NewAgent, 'id'>;

type AgentFormGroupContent = {
  id: FormControl<IAgent['id'] | NewAgent['id']>;
  matricule: FormControl<IAgent['matricule']>;
  nom: FormControl<IAgent['nom']>;
  prenom: FormControl<IAgent['prenom']>;
  dateNaissance: FormControl<IAgent['dateNaissance']>;
  telephone: FormControl<IAgent['telephone']>;
  fonction: FormControl<IAgent['fonction']>;
  direction: FormControl<IAgent['direction']>;
  gestion: FormControl<IAgent['gestion']>;
  user: FormControl<IAgent['user']>;
};

export type AgentFormGroup = FormGroup<AgentFormGroupContent>;

@Service()
export class AgentFormService {
  createAgentFormGroup(agent?: AgentFormGroupInput): AgentFormGroup {
    const agentRawValue = {
      ...this.getFormDefaults(),
      ...(agent ?? { id: null }),
    };

    return new FormGroup<AgentFormGroupContent>({
      id: new FormControl(
        { value: agentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      matricule: new FormControl(agentRawValue.matricule, {
        validators: [Validators.required],
      }),
      nom: new FormControl(agentRawValue.nom, {
        validators: [Validators.required],
      }),
      prenom: new FormControl(agentRawValue.prenom, {
        validators: [Validators.required],
      }),
      dateNaissance: new FormControl(agentRawValue.dateNaissance),
      telephone: new FormControl(agentRawValue.telephone),
      fonction: new FormControl(agentRawValue.fonction),
      direction: new FormControl(agentRawValue.direction),
      gestion: new FormControl(agentRawValue.gestion),
      user: new FormControl(agentRawValue.user),
    });
  }

  getAgent(form: AgentFormGroup): IAgent | NewAgent {
    return form.getRawValue();
  }

  resetForm(form: AgentFormGroup, agent: AgentFormGroupInput): void {
    const agentRawValue = { ...this.getFormDefaults(), ...agent };
    form.reset({
      ...agentRawValue,
      id: { value: agentRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AgentFormDefaults {
    return {
      id: null,
    };
  }
}
