export interface ParsingResponse {
  //int newSubmissions, int oldSubmissions, Map<String, Integer> detectedLanguages
  newSubmissions: number
  oldSubmissions: number
  detectedLanguages: Record<string, number>
}