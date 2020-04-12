export class Clarification {
  id: string;
  recipient: string; //   -> "team1" OR "all"
  problem: string;
  question: string;
  answer: string;
  time: number;
  isAnswered: boolean;
}
