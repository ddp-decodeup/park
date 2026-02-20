abstract class Routes {
  /// initial routes
  static const brandPage = "/";
  static const splash = '/splash';

  /// auth routes
  static const login = '/login';
  static const forgotPassword = '/forgotPassword';

  /// main routes
  static const home = '/home';

  /// ticketing routes
  static const ticketing = '/ticketing';
  static const ticketIssue = '/ticketing/issue';
  static const ticketIssuePreview = '/ticketing/issue/preview';
  static const scan = '/ticketing/scan';
  static const municipalCitation = '/ticketing/municipalCitation';
  static const municipalCitationPreview = '/ticketing/municipalCitation/preview';
  static const citationResult = '/ticketing/citationResult';
  static const payByPlate = '/ticketing/payByPlate';
  static const payBySpace = '/ticketing/payBySpace';
  static const ticketPreview = '/ticketing/ticketPreview';

  /// my activity routes
  static const myActivity = '/myActivity';
  static const graphView = "/myActivity/graphView";
  static const dailySummary = "/myActivity/dailySummary";
  static const lprHits = "/myActivity/lprHits";

  /// scan routes
  static const manualEntry = '/manualEntry';

  /// settings routes
  static const settings = '/settings';

  /// reports routes
  static const reports = '/reports';
}
